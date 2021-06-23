# Tournament web application

The application runs on port 8080 and is available on http://localhost:8080/.

## Database

The application uses in-memory database, but it's persisted to the file, so if you close the app and run again you will see all previous data. The database is saved to current working directory under directory data, so whenever you need a fresh DB, you can delete it.

If you want to see the data, you can access them via http://localhost:8080/h2-console/ where:
* database: ./data/tournament
* username: sa
* password: password

## TODO

There are implemented some unit, integration and smoke tests, but they aren't covering everything, just to demonstrate unit testing with Mockito, then repository testing with in-memory DB and also a controller is covered by (semi-)unit and integration tests.