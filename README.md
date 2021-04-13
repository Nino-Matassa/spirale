# spirale
Spirale is named after the French drama Spirale.

The app collates WHO data for COVID-19 globally.
wich is available from: 

https://covid19.who.int/info 
This page contains two CSV files:
https://covid19.who.int/WHO-COVID-19-global-data.csv and 
https://covid19.who.int/WHO-COVID-19-global-table-data.csv

The CSV files are downloaded when new files are available, normally twice a day, or on installation. Choosing Invalidate from the menu will 
force the files to be re-downloaded. An in memory database is then created for the app to use.
There are two reasons to hold the database in memory:

a. Building the database in storage takes more time, by a factor of thousands. The last test build was approximately 3 hours vs. less than one
minute to build it in memory.

b. The WHO retrospectively update the data regularly. E.g. Cases or deaths reported on any given day can be from an earlier time or if and
when criterion is altered.

Unfortunately: As the Details CSV file grows to acommodate each days new data the build time increases accordingly by 200+ lines per day.

The app exclusively uses WHO data, all input is automated. Additional calculated fields have been added:

Population:  Total Cases / Case per 100,000 * One hundred thousand.

Active Cases: All new cases reported in the last 28 days.

Total Infected: The total number of cases relative to the population.

R-Nought: cases for today / cases for yesterday for the last 28 days.


