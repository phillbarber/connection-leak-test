
![Architecture Diagram](architecture-diagram.png)

1. User requests the health-check page of our Connection Leak App.  The health-check resource reports on the application's health by testing all down stream dependencies.  In this case, our app depends on useful service.
2. The code then sends a request to the version resource of the Useful service.
3. Our app receives a 200 response with the version of the service in the body "1.1".  Our app fails to close the connection that was used to send this request.
4. Our app responds with a 200 status code and some json stating that the app is OK.
