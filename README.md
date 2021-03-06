# MyTimesheetApp

Timesheet Android Application designed by Weijie

# User Story
- Simple looking, sleek, fast loading is top priority.
- Facebook and email logging and sign-up feature.
- Homepage to view/add/edit/delete/share current user’s timesheets.
- Detail activity to edit current selected timesheet.
- Table of timesheets to display timesheet which can be ordered and selected.
- Generate summaries for selected timesheet. (Any selected/ time range based)
- Dropbox sharing mechanism. (revoke will generate new TID beside the original TID)

# Requirements and Assumptions
- If a Timesheet is shared editable by 2 users, they will modify on the same timesheet. (Same TID)
- If a Timesheet is revoked, there will be another version of timesheet(new TID) generated in database owned by user 2. If user 1 share the same doc with user 2 again, it will generate another copy on user 2’s sharing list.
- Timesheet can only be shared individually.
- If the timesheet share request is still pending, it will not be visible to that person.

# Key Issues
- Facebook/Email authentication. (Firebase)
- Dropbox file sharing mechanism to maintain timesheet.
- Every timesheet record have two list of UIDs who can view only/edit it.
- Every user have two(or three) lists of TIDs, one is owned by user, another is shared by user(view only/edit)
- The way to generate summary and how it displayed in the client app.
- Accept sharing situation.(Push notification?)

# UI Design
- Launch screen(optional)
- Logging screen
- Sign-up screen
- Homepage(Control center): Add/share/view
- Timesheet screen(my timesheet, timesheet shared by others)
- Detail Screen (Edit Timesheet)
- Summary Report Screen

# Backend
- BaaS(Firebase): backend as a service.
- Firebase: support realtime database, logging authentication, data storage and cloud messaging.

# Data Storage
- Timesheet table: store all the timesheets generated by user with auto increment TID.
- User table: store all the user profile with auto increment UID.
- Twitter like Share data structure.

# Consistency and Availability
- Consistency: What if two user are trying to update the sharing timesheet at the same time. (Google doc-realtime update/dropbox- generate conflict version/ Github – solve conflict before merge).
- Availability: The system should handle all the user request at the same time within certain amout of time.
