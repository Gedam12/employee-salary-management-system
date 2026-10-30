
import csv
import random
from pathlib import Path

TOTAL_EMPLOYEES = 10000
OUTPUT_FILE = Path(__file__).with_name("employees_seed.csv")

DEPARTMENTS = [
    "Engineering",
    "Finance",
    "Human Resources",
    "Marketing",
    "Sales",
    "Operations",
    "IT",
    "Customer Support",
    "Legal",
    "Product"
]

COUNTRIES = [
    "United States",
    "India",
    "United Kingdom",
    "Canada",
    "Germany"
]

FIRST_NAMES = [
    "Alex", "Sarah", "John", "Priya", "Michael",
    "Emily", "David", "Ananya", "Daniel", "Sophia"
]

LAST_NAMES = [
    "Johnson", "Williams", "Smith", "Patel",
    "Brown", "Sharma", "Davis", "Wilson",
    "Taylor", "Anderson"
]

random.seed(42)

with OUTPUT_FILE.open("w", newline="", encoding="utf-8") as file:
    writer = csv.writer(file)

    writer.writerow([
    "name",
    "email",
    "department",
    "salary"
])

    for employee_id in range(1, TOTAL_EMPLOYEES + 1):
        first_name = random.choice(FIRST_NAMES)
        last_name = random.choice(LAST_NAMES)

        name = f"{first_name} {last_name}"

        email = (
            f"{first_name.lower()}."
            f"{last_name.lower()}."
            f"{employee_id}@acme.example"
        )

        department = random.choice(DEPARTMENTS)
        country = random.choice(COUNTRIES)

        salary = random.randint(45000, 180000)

        writer.writerow([
    name,
    email,
    department,
    salary
])

print(f"Generated {TOTAL_EMPLOYEES} employees")
print(f"Saved to: {OUTPUT_FILE}")
