# A Java Android Program / Application

**This is an Android app from my days as a Computer Science student**

_This programm was created for the seventh semester class Modern Software Technology Subjects 
and is one of the mandatory projects for the class_

> #### Description of project
>
>>A Java Android application that simulates the functionality of a speedometer using GPS by measuring distance and time elapsed to find the speed and saves violations of max speed in SQLite.

> #### Implementation of project
>
> 1. The app should be able to display the speed of the mobile via GPS if the app is open
> 2. You need to integrate where appropriate run-time permissions are needed
> 3. The user will set speed limits, which if overridden will be recorded in the NW (where the event took place):
>> - Timestamp
>> - Speed
>> - Location
>
> 4. There will be a list of POIs (Point of Interest) stored where the following data will be recorded for each point:
>> - Title of Location
>> - Description
>> - Category
>> - Coordinates
>
> 5. In addition, the user will also specify a radius (distance in meters) within which he will want to be notified when approaching one or more of the POIs he has set
> 6. Although found within one specified radius from one or more POIs, the following will occur:
>> - It will receive a notification message that this POI is nearby
>> - The event will be recorded in the NW (timestamp, POI, location)
>
> 7. Finally, there will be the ability to view statistics from the data collected by the application and recorded in the NW, both for speeding exceedances and for being near POIs. Try to show as "useful" to the user the statistics you can.

> #### About this project
>
> - To calculate speed I don't use the method .getSpeed(), but for an interval (t), I measure the distance (d) and the speed will be s=d/t
> - The comments to make the code understandable, are within the .java archives
> - This project was written in Android Studio
> - This repository was created to show the variety of the work I did and experience I gained as a student
>
