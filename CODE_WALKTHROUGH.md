# Code Walkthrough — Employee Task Manager (Day 2)

## 1. Project Information

- Project: Employee Task Manager
- Language: Kotlin
- UI: Android Views with XML layouts
- Package: com.example.employeetaskmanager

## 2. Application Launch Flow

1. Android reads AndroidManifest.xml.
2. MainActivity is identified as the launcher activity through MAIN and LAUNCHER.
3. Android creates MainActivity.
4. MainActivity.onCreate() is called.
5. super.onCreate(savedInstanceState) initializes the parent activity.
6. setContentView(R.layout.layout) loads the Home screen.

## 3. MainActivity

File: app/src/main/java/com/example/employeetaskmanager/MainActivity.kt

MainActivity performs these operations:

- onCreate(): Activity initialization callback.
- super.onCreate(): Calls parent activity initialization.
- setContentView(): Loads the Home screen XML.
- findViewById<Button>(): Finds the Task Details button.
- setOnClickListener(): Registers the button click callback.
- Intent(this, TaskDetailsActivity::class.java): Creates an explicit Intent.
- startActivity(intent): Opens TaskDetailsActivity.

Kotlin concepts:
- val declares a read-only variable reference.
- fun declares a function.
- class defines a class.
- Bundle? indicates a nullable Bundle parameter.

## 4. Home Screen UI

File: app/src/main/res/layout/layout.xml

The Home screen contains:
- A LinearLayout as the root layout.
- A TextView displaying the welcome message.
- A Button with ID buttonTaskDetails.

The button label is loaded from strings.xml using @string/task_details_button.

## 5. Task Details Activity

File: app/src/main/java/com/example/employeetaskmanager/TaskDetailsActivity.kt

TaskDetailsActivity extends AppCompatActivity.

Its onCreate() method:
1. Calls super.onCreate(savedInstanceState).
2. Loads R.layout.activity_task_details.
3. Displays the Task Details heading and description.

The screen layout is defined in activity_task_details.xml.
Its text is stored in strings.xml.

## 6. Manifest Registration

AndroidManifest.xml registers TaskDetailsActivity:

    <activity
        android:name=".TaskDetailsActivity"
        android:exported="false" />

MainActivity remains the launcher activity.

## 7. Home to Task Details — Actual Click Flow

The Task Details screen and navigation were added as a sample implementation for the Day 2 exercise.

Actual flow:

1. User launches the application.
2. Android resolves MainActivity through AndroidManifest.xml.
3. MainActivity.onCreate() executes.
4. setContentView(R.layout.layout) displays the Home screen.
5. findViewById() finds buttonTaskDetails.
6. setOnClickListener() registers the click callback.
7. User taps Task Details.
8. An explicit Intent targeting TaskDetailsActivity is created.
9. startActivity(intent) launches TaskDetailsActivity.
10. TaskDetailsActivity.onCreate() executes.
11. setContentView(R.layout.activity_task_details) displays the screen.
12. User presses Back and returns to MainActivity.

## 8. Navigation Diagram

    App Launch
       |
    AndroidManifest.xml
       |
    MainActivity
       |
    onCreate()
       |
    setContentView(R.layout.layout)
       |
    Home Screen
       |
    findViewById(buttonTaskDetails)
       |
    setOnClickListener()
       |
    User taps Task Details
       |
    Create explicit Intent
       |
    startActivity(intent)
       |
    TaskDetailsActivity
       |
    onCreate()
       |
    setContentView(R.layout.activity_task_details)
       |
    Task Details Screen
       |
    Back button
       |
    Home Screen

## 9. Testing and Verification

Verified on Redmi Note 8 Pro:

- Home screen opened successfully.
- Task Details button was visible.
- Tapping the button opened TaskDetailsActivity.
- Task Details heading and description displayed correctly.
- Back navigation returned to the Home screen.
- Debug APK build completed successfully.
- APK installation through ADB completed successfully.

## 10. Conclusion

The Day 2 Home-to-Task-Details navigation exercise is implemented and tested.

This sample implementation demonstrates Activity lifecycle initialization, Kotlin variables and functions, XML layouts, string resources, button click listeners, explicit Intents, startActivity(), and AndroidManifest.xml activity registration.
