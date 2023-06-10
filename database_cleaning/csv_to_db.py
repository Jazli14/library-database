import csv
import psycopg2

# Database credentials
db_host = "ruby.db.elephantsql.com"
db_port = "5432"  # Default PostgreSQL port
db_name = "agclswdr"
db_username = "agclswdr"
db_password = "JhvkCaxHwI44WdwbFtNo3GMpyg66xwVR"

# CSV file path
csv_file_path = "books_string.csv"


# Database connection
connection = psycopg2.connect(
    host=db_host,
    port=db_port,
    dbname=db_name,
    user=db_username,
    password=db_password
)
cursor = connection.cursor()

# Create table
create_table_query = """
CREATE TABLE IF NOT EXISTS books (
    bookID INT,
    title VARCHAR(255),
    authors VARCHAR(255),
    rating DOUBLE,
    num_pages INT,
    year INT,
    ready BOOLEAN
)
"""
cursor.execute(create_table_query)

# Upload CSV data
with open(csv_file_path, 'r', encoding='utf-8') as file:
    reader = csv.reader(file)
    next(reader)  # Skip the header row
    for row in reader:
        book_id = int(row[0])  # Convert bookID to integer
        title = row[1]  # No conversion needed for VARCHAR
        authors = row[2]  # No conversion needed for VARCHAR
        rating = float(row[3])  # Convert rating to float
        num_pages = int(row[4])  # Convert num_pages to integer
        year = int(row[5])  # Convert year to integer
        ready = True

        cursor.execute(
            f"INSERT INTO books (book_id, title, authors, rating, num_pages, year, ready) "
            "VALUES (%s, %s, %s, %s, %s, %s, %s)",
            (book_id, title, authors, rating, num_pages, year, ready)
        )


# Commit changes and close connection
connection.commit()
cursor.close()
connection.close()

print("CSV data uploaded successfully.")
