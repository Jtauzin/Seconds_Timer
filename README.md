# Welcome to the Timer Extreme!

## Description

Timer Extreme is a seconds timer that accepts user input as an Integer representing seconds. There
is a start button to start the timer, and a stop button to reset the timer. Because watching a timer
count down to zero and have nothing happen is woefully unsatisfying there is a lovely surprise at
the end, whether it is stopped or reaches 0.

The timer can take a maximum of 9,999,999 seconds which is 166,666.65 minutes, or 2,777.78 hours,
or 115.74 days, or about 16.53 weeks, or about 4 months, which is about a third of a year.

I used this to soft boil an egg - 375 seconds. 10/10 would boil again.

Cool tools used:

• ViewBinding: This is part of the jetpack library and eliminates the need to find view by id. Instead, we can link our binding variable to our layout and call views directly using dot notation to access them, like how we access an objects variables or functions. This eliminates the possibility of null errors or type errors.

• ViewModel: Kotlin’s ViewModel class allows us to create a custom ViewModel where we can declare MutableLiveData which can be updated asynchronously and monitored by the UI thread. In this way our ViewModel updates data, and our Activity adjusted the UI based on the new data. This allows for a very reactive application.

• Coroutines: Kotlin’s Coroutines are “cheaper” than threads and allowed for a custom timer implementation where we can declare how we want our thread to be handled using Dispatchers. In the end, it made sense to switch from Dispatchers.IO to Dispatchers.Default from a design standpoint. While both allow for work on a background thread, Dispatchers.Default is geared toward CPU intensive work like sorting lists, parsing data, or in our case constant mathematical operations and updates. Dispatchers.IO is optimized for network calls, reading files, and working with our local storage, etc. Both technically work in this use case, but default is more “correct” in my opinion. I would love to hear yours!
