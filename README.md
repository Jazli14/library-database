# Library Book SQL Database System
This project is an attempt to make an app featuring a system that allows users to register an account to loan a book for a certain time period and return them as well. It also features a system to create an admin account to control the database’s books, loans and accounts.

## Creating the Database
We used an online server that hosted a PostgreSQL database called ElephantSQL which allowed us to create a system where all users interacted with the same database. We used the JDBC API to access the database through Java with the given database connection credentials.

##  Cleaning and uploading a dataset of books
Once the data is gathered, it needs to be put through preprocessing before the model is trained.

A dataset was found on Kaggle from this link: [Kaggle Books Dataset](https://www.kaggle.com/datasets/jealousleopard/goodreadsbooks "Go to books dataset"). 

After downloading the dataset, we cleaned it by extracting certain attributes/fields such as the book ID, title, author, rating, number of pages, and publishing date and removed entries that had missing data. We changed the publishing date to year to simplify the attribute and did all of this through a simple python script. To upload the dataset, we used another python script called “csv_to_db.py” to read the csv and to the dataset to the database through the psycopg2 database adapter to connect to our online database and import it using an SQL insert query.

## Code Structure
The project uses an MVC (Model-View-Controller) architectural pattern to organize and structure the application. It uses JavaFX and FXMLs to design and incorporate the GUI to interact with the business logic/controllers including the interaction with the database. The app will access the database to create a ResultSet, and the code will loop through it to create a local copy of the table. When there is a need to update the models/entities, it will use SQL queries to mimic the local copy and modify, delete or create entries within the real database. Since the books, loans and accounts are placed within a HashMap; inserting, deleting and accessing are near instant to allow fast access to the data.

## Login System
The program offers two ways to interact with the database, one as a regular user and another as an admin. So we implemented a login and registration system to accommodate this, where they can either choose to select “User” or “Admin” by switching the tab where they provide a username and password. If they do not have an account they can press register to create an account or directly login. These accounts are also stored on the database where the admin can view them.

![image](/resources/login_interface.png)

## User Account Interface
Once the user logs in, they are greeted with an interface that shows books the user can loan out with a search bar. It will also feature a tab that allows them to see any current loans they have.

![image](/resources/user_interface.png)

## Admin Account Interface
The admin will log in to be greeted to the same interface but with extra options. It is allowed three different tabs instead, the book table, loans table but as well as access to an accounts table. They will also have access to hidden fields in an entry such as the “book ID” or the “loan ID”.

![image](/resources/admin_interface.png)

## Tables and Search

### Books Table
Both a user and admin will have a table of books that will be displayed directly under the search bar which displays the books title, author, a rating out of 5, number of pages, the year it was published and also if the book was available to loan to the user. If a user account is using the system they are allowed to loan books that are available to them, using the date pickers at the bottom. This will update the book’s availability and create a loan in the database for that book. An admin will be allowed to delete, create and edit books. The latter two functions will prompt a window to open up to add or change a title, author, rating, number of pages, year and if it's available. 

![image](/resources/books_example.png)

### Loans Table
On the left there is an option to switch the tab to find loans for the user. It will display the book title, borrower which should be the user and also a borrow and return date. It will also display if that loan is overdue, that is if the current date when the user logs in is past the return date. Similar to the books tab, If an admin account uses the system they will instead have access to removing a loan, editing a loan and creating a loan. The admin will be prompted again with a window to input a book ID, title, borrower, loan period and also if it is overdue. 

![image](/resources/loans_example.png)

### Accounts Table
A table of accounts will be displayed with fields such as username, password and if they have admin privileges. Although within the password column the admin cannot see the passwords of the accounts as they will be masked off with “••••••••••” for enhanced security as a good practice. The admin that views this table will also have access to a button to remove an account or create an account. If they press the latter they will be prompted with a window to input a username, password if the account will be an admin. Both these features will update to the SQL database in real time. 

![image](/resources/accounts_example.png)

### Search Feature
The interface also features a search system for a certain book based on the title, author, maximum or minimum rating through a slider, the length of the book of a drop-down menu, the year and if the user wants to see books that are loaned out/unavailable to them. Both the user and admin will have access to search for books. Only the admin will have the ability to search for loans, they can search for the book title, the account that borrowed the loan, the borrow and return date and as well as if it is overdue. Similar to the loans table, the admin once again will be allowed to search for certain accounts with a certain username and if they are an admin. This feature heavily uses SQL  to design and build a specific query based on the user/admin's choices to return a table filled with the appropriate entries.
