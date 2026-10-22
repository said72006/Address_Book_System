import pathlib
import subprocess
import tempfile
import unittest


ROOT = pathlib.Path(__file__).resolve().parents[1]
CLASSPATH = str(ROOT / "build")


def execute(data, *, existing=None):
    with tempfile.TemporaryDirectory() as temp:
        directory = pathlib.Path(temp)
        if existing is not None:
            (directory / "contacts.txt").write_text(existing, encoding="utf-8")
        result = subprocess.run(["java", "-cp", CLASSPATH, "AddressBook"],
                                input=data, text=True, cwd=temp,
                                capture_output=True, timeout=8)
        contacts = (directory / "contacts.txt")
        return result, contacts.read_text(encoding="utf-8") if contacts.exists() else ""


class AddressBookTests(unittest.TestCase):
    def test_invalid_menu_choice_does_not_crash(self):
        proc, _ = execute("not-a-number\n7\n")
        self.assertEqual(proc.returncode, 0, proc.stderr)
        self.assertIn("Invalid choice", proc.stdout)

    def test_contact_and_duplicate(self):
        proc, contacts = execute("1\nAlice\nWork\n+123456789\n1\nBob\nWork\n+123456789\n7\n")
        self.assertEqual(proc.returncode, 0, proc.stderr)
        self.assertIn("already exists", proc.stdout)
        self.assertEqual(contacts.count("+123456789"), 1)

    def test_bad_line_is_skipped(self):
        proc, _ = execute("6\n7\n", existing="bad-record\nAlice|Work|+123456789\n")
        self.assertEqual(proc.returncode, 0, proc.stderr)
        self.assertIn("Skipping malformed", proc.stdout)
        self.assertIn("Alice", proc.stdout)

    def test_unexpected_end_of_input_exits_cleanly(self):
        proc, _ = execute("")
        self.assertEqual(proc.returncode, 0, proc.stderr)
        self.assertIn("Input closed.", proc.stdout)

    def test_invalid_phone_rejected(self):
        proc, records = execute("1\\nAlice\\nWork\\n---\\n7\\n".replace("\\n", "\n"))
        self.assertEqual(proc.returncode, 0, proc.stderr)
        self.assertIn("Invalid phone number", proc.stdout)
        self.assertEqual(records, "")

    def test_contact_file_private_on_posix(self):
        import os
        import stat
        if os.name != "posix":
            self.skipTest("POSIX-only permission assertion")
        with tempfile.TemporaryDirectory() as directory:
            proc = subprocess.run(["java", "-cp", CLASSPATH, "AddressBook"],
                input="1\nAlice\nWork\n+123456789\n7\n",
                text=True, cwd=directory, capture_output=True, timeout=8)
            self.assertEqual(proc.returncode, 0, proc.stderr)
            mode = stat.S_IMODE((pathlib.Path(directory) / "contacts.txt").stat().st_mode)
            self.assertEqual(mode & 0o077, 0, "Contacts should not be readable by group/others")

    def test_empty_search_is_rejected(self):
        proc, _ = execute("2\n\n7\n")
        self.assertEqual(proc.returncode, 0, proc.stderr)
        self.assertIn("Name cannot be empty", proc.stdout)


if __name__ == "__main__":
    unittest.main()
