# spirale
Spirale is named after the French drama Spirale.

The app collates WHO data for COVID-19 globally.
wich is available from: 

https://covid19.who.int/info 
This page contains two CSV files:
https://covid19.who.int/WHO-COVID-19-global-data.csv and 
https://covid19.who.int/WHO-COVID-19-global-table-data.csv

The files are downloaded when newer files are available, normally twice a day, or on installation. Choosing Invalidate from the menu will force the files to 
re-downloaded, then ann in memory database is then created for the app to use.
The database is held in memory because the WHO sometimes retrospectively updates some of the data contained in them. So the initialisation time can take a minute
or so depending on the device being used. In my case a Samsung J5.

The intent behind the application is to present unadulterated WHO data. To that end it is WHO data.

The primary reason for developing the app was to keep informed of world wide COVID trends but also to be able to see which countries benifit from the vaccine
and where the virus is most prevelant etc.

Active Cases is the addition of all cases for the previous 28 days.

R-Nought is the average {rNought = current / (double)previous;} over 28 days, there are no other factors included. The 28 day average results in an accurate trend,
the R-Nought value itself is always questionable.

Total Infected is the precentage of the population the total number of cases represent and does not take into account contracting the virus more than once.




