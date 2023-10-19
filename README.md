# CS453-ExpenseTrackingApp

This was originally a project for the CS453 Mobile Programming course at CSUEB. The goal of the project was to create an Android application and learn how to launch activities and use intents and store data using SharedPreferences.

This project was refactored to use the Hilt library for dependency injection and the Room library for a local SQLite database instead of SharedPreferences. This update also added some simple data visualization.

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

Shows the app with main screen with several expenses already added.

<img src="https://github.com/LordArugula/ExpenseTrackingApp/assets/41593388/83aef897-d285-4989-bb45-01c8b1b95ad8" width="240" />

### Summary screen

Shows a pie chart of the expenses based on their categories and the total and average amount spent per expense.

<img src="https://github.com/LordArugula/ExpenseTrackingApp/assets/41593388/8f1a865f-6313-47d0-b3bd-5109d122e612" width="240" />

The chart with a date filter applied.

<img src="https://github.com/LordArugula/ExpenseTrackingApp/assets/41593388/3904b0b1-6407-4c36-b2fe-1f8addb15c1b" width="240" />

### Filter screen

Clicking on the filter button opens a side menu where the user can apply filters on which expenses are displayed. The user can filter by a date range and/or by category.

<img src="https://github.com/LordArugula/ExpenseTrackingApp/assets/41593388/aef9d146-c3ac-46df-92ed-7e0fde8e069e" width="240" />

### Sort screen

Clicking on the sort button opens a side menu where the user can change the sorting of the expenses.

<img src="https://github.com/LordArugula/ExpenseTrackingApp/assets/41593388/1cca8695-4d0c-4145-aede-5244144752c9" width="240" />

### Expense details screen

The user can press the add button at the bottom corner of the main screen to add a new expense entry.

<img src="https://github.com/LordArugula/ExpenseTrackingApp/assets/41593388/cbead208-7fd1-4f0d-a3f4-293d154cd278" width="240" />

The user can also tap on an existing expense to view, edit, and delete an expense entry. 

<img src="https://github.com/LordArugula/ExpenseTrackingApp/assets/41593388/4c6e9dc1-6e25-4cbe-9ae7-fd892ea80aa9" width="240" />

Using the predefined categories:

<img src="https://user-images.githubusercontent.com/41593388/218354419-2a620611-26bf-4aca-942d-9e188a1ae9d4.png" width="240" />

The user can also use one of the predefined categories or create a custom category by entering a new name.

<img src="https://user-images.githubusercontent.com/41593388/218354308-e5ba5bb7-8e5c-4c61-86dd-a53de2e9d149.png" width="240" />
