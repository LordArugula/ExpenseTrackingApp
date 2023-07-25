# CS453-ExpenseTrackingApp

This was a project for the CS453 Mobile Programming course at CSUEB. The goal of the project was to create an Android application and learn how to launch activities and use intents and store data using SharedPreferences.

This project was refactored to use OOP best practices and follow SOLID principles to improve testability and extensibility. The project now uses the Hilt library for dependency injection and the Room library for a local SQLite database instead of SharedPreferences. 

## Requirements

The user needs to be able to enter and track their expenses by day, month and year.

Tracking includes the ability to see totals and averages by day, month and year for each
category of expense.

Totals for day, month and year also needs to be shown for all categories combined.
Each expense needs to be entered and details about each expense should be captured.

Each expense should include:
* Expense Date
* Name of Expense
* Category
* Cost
* Reason
* Notes

You need to have a way for the user to create a new entry, update an existing entry and
delete an entry.

The user should be able to select from already existing categories stored.

There should also be a way to enter a new expense category.

Your app design needs to contain at least two different screens. More screens will probably
be needed to space out the features appropriately.

Design the user interface that uses an Adapter and a RecyclerView, ListView, GridView or another View component of your choice. 

## Screenshots

### Main screen 

Shows the app with several expenses added. Can see the total and average money spent.

<img src="https://user-images.githubusercontent.com/41593388/218352898-a6a2d926-2251-40c3-be40-154320c38be1.png" width="240" />

### Filter screen

Clicking on the filter button opens this menu where the user can apply filters on which expenses are displayed. The user can filter by a date range and/or by category.

<img src="https://user-images.githubusercontent.com/41593388/218352915-a892e815-5a71-410a-a4a4-c2be29ab7100.png" width="240" />

Choosing a date to filter by.

<img src="https://user-images.githubusercontent.com/41593388/218353073-26776f3a-9763-4887-a342-68a3f2c1659a.png" width="240" />

The main screen with only the expenses that match the filter.

<img src="https://user-images.githubusercontent.com/41593388/218353141-1cfed06d-b061-41c1-a48c-026471dcda6a.png" width="240" />

### Expense details screen

The user can press the add button at the bottom corner of the main screen to add a new expense entry.

<img src="https://user-images.githubusercontent.com/41593388/218353307-818a0e84-1007-4b6c-ad83-aa2a15fb1571.png" width="240" />

After adding the new entry

<img src="https://user-images.githubusercontent.com/41593388/218353400-bcee747c-909d-47b6-8cbb-4c5664bbe99f.png" width="240" />

The user can also tap on an existing expense to view, edit, and delete an expense entry. 

<img src="https://user-images.githubusercontent.com/41593388/218353580-a3a104e5-759c-401d-86b7-966da912c5d1.png" width="240" />

Using the predefined categories:

<img src="https://user-images.githubusercontent.com/41593388/218354419-2a620611-26bf-4aca-942d-9e188a1ae9d4.png" width="240" />

The user can also use one of the predefined categories or create a custom category by entering a new name.

<img src="https://user-images.githubusercontent.com/41593388/218354308-e5ba5bb7-8e5c-4c61-86dd-a53de2e9d149.png" width="240" />
