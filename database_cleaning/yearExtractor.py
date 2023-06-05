import csv


def extract_year(date):
    parts = date.split('/')
    if len(parts) >= 3:
        return parts[2]
    return ''


def update_csv(file_path):
    updated_rows = []
    with open(file_path, 'r', encoding='utf-8') as file:
        reader = csv.DictReader(file)
        headers = reader.fieldnames
        for row in reader:
            year = extract_year(row['year'])
            row['year'] = year
            updated_rows.append(row)

    with open(file_path, 'w', newline='', encoding='utf-8') as file:
        writer = csv.DictWriter(file, fieldnames=headers)
        writer.writeheader()
        writer.writerows(updated_rows)




# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    # Usage
    csv_file_path = 'books.csv'
    update_csv(csv_file_path)

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
