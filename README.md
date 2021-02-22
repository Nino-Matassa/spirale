# spirale
Spirale is named after the French drama Spirale.

The app collates WHO data for COVID-19 globally.
wich is available from: 

https://covid19.who.int/info 
This page contains two CSV files:
https://covid19.who.int/WHO-COVID-19-global-data.csv and 
https://covid19.who.int/WHO-COVID-19-global-table-data.csv

The files are downloaded either on the first run when they aren't there or if they've been updated. An in memory database is then created for the app to use.
The database is held in memory because the WHO sometimes retrospectively updates some of the data contained in them. So the initialisation time can take a minute
or so depending on the device being used. In my case a Samsung J5.

The intent behind the application is to present unadulterated WHO data. To that end it is WHO data.

Primairaly I wrote the app for my own use so if you play with it a little you'll get the hang of it quick enough, it's also worth pointing out this is my first
Android application, and my inexperience with the platform has it's effect. The app is generally stable... Relaunching it from the background causes it to
appear to hang, this is notable when it downloads new data with the database rebuild taking time. Other than that it works just fine and presents the data as is.

The primary reason for developing the app was to keep informed of world wide COVID trends but also to be able to see which countries benifit from the vaccine
and where the virus is most prevelant etc.

I'll update the readme as I go along. If you want to contact me, nino.matassa@gmail.com.
