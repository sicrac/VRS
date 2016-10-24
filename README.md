# VIDEO RENTAL STORE

Constraints/Semplifications:

  - **One only ongoing rental per customer at a time.**  
    Before renting new films a customer must return the already rented ones.
  - **Returning films can't be partial.**  
    A customer who rented 6 films can't return e.g. one day 4 films and another day the remaining 2, he must return 6 films the same day.

# REST API

It exposes resources for three operations:

  - **Searching**
  - **Renting**
  - **Returning**

Post data, where needed, must be url-encoded while the response body is in *JSON* format for all the resources.

### Searching
#
> METHOD `GET`  
> PATH `/api/films`  
>  QUERY STRING `kind=<kind>&search=<search>&availableOnly=<availableOnly>`  

`kind` must be one of the values (`OLD`, `REGULAR`, `NEW`)  
`search` is a sequence of characters  
`availableOnly` must be one of the values (`false`, `true`)

All the query parameters are optional and `availableOnly` defaults to `false`.

### Renting
#
> METHOD `POST`  
> PATH `/api/films/<username>`  
> DATA `filmId=<filmId_1>&...&filmId=<filmdId_N>&days=<days>`  

`filmId` must be an integer  
`days` must be an integer

Post data parameters are both mandatory.  
It returns a **403 Forbidden** error response in case the customer has an ongoing rental (i.e. he has not yet returned films).

### Returning
#

> METHOD `PUT`  
> PATH `/api/films/<username>/<rentalId>`  
> DATA `currentDate=<currentDate>`  

`currentDate` must be expressed with format `yyyy-MM-dd`.  
This parameter is optional and it is here for testing purpose only.  
It allows to force the current date in the future and see what surcharge should be payed.  
This endpoint returns a **404 Not Found** error response in case the customer hasn't an ongoing rental or the `<rentalId>` isn't the right one.

# USAGE

    git clone https://github.com/zimonc/VRS.git
    cd VRS
    mvn spring-boot:run

# TESTING

A *basic authentication* is provided with a simple in-memory implementation.
The following are valid customers:

- username=password=customer1
- username=password=customer2
- username=password=customer3

To test the application you can use any client tool able to send *http* requests.  
Below are some examples by using `curl`.  

### Searching

Getting the complete list of films:

    curl -u customer1:customer1 "http://localhost:8080/api/films"

Getting the list of `OLD` available films with title containing the `Wizard` string:

    curl -u customer1:customer1 "http://localhost:8080/api/films?kind=OLD&search=Wizard&availableOnly=true"
    [{"id":1,"title":"The Wizard of Oz","kind":"OLD","available":true}]

### Renting

Renting films with ids 8, 9, 12, 15 and 1234 (who doesn't exists):

    curl -u customer1:customer1 "http://localhost:8080/api/films/customer1" -d "days=8&filmId=9&filmId=12&filmId=15&filmId=1234"
    {"rentalId":1, "price":240.00,"details":[{"filmId":9,"title":"Metropolis","flag":"UNAVAILABLE","price":0},{"filmId":12,"title":"Singin' in the Rain","flag":"AVAILABLE","price":120.00},{"filmId":15,"title":"Snow White and the Seven Dwarfs","flag":"AVAILABLE","price":120.00},{"filmId":1234,"title":null,"flag":"NOT_FOUND","price":0}],"success":true,"errorMessage":null,"endRentalDate":"2016-10-31","totalBonus":7,"rentalBonus":4}

The details array shows rental informations for every film.

### Returning

Returning films of the above transaction, `mocking` the current date to be in the
future to see the working the surcharge machinery:

    curl -u customer1:customer1 "http://localhost:8080/api/films/customer1/1" -X POST -d "currentDate=2016-11-04"
    {"rentalId":1, "details":[{"filmId":12,"title":"Singin' in the Rain","flag":"RETURNED","price":120.00},{"filmId":15,"title":"Snow White and the Seven Dwarfs","flag":"RETURNED","price":120.00}],"success":true,"errorMessage":null,"endRentalDate":"2016-10-31","returningDate":"2016-11-04","surcharge":240.00}

# NOTES
A **DSL** (Data Service Layer) is used to access data while hiding the storage details 
and this is achieved by implementing the `VideoRentalStoreDSL` interface.  
For the purpose of the exercise and to make things easier I've implemented
an `InMemoryDAL` class who indeed, relies on in-memory objects built after
reading films data from films.csv while customers data are (brutally) embedded.
The `VideoRentalStoreDSL` (thus `InMemoryDAL`) lacks the save/update method that 
I've omitted to simplify.  
The **application.properties** file allow to change the parameters given by the problem.
Changing the default currency (from `SEK`) actually doesn't produce any result, but it would produce  
allowing customers to specify a different currency from the default one.  
To this end we should (among other things) implement the `RentalStoreService.calculateConversionRate`  
who currently works with only the the default currency.
