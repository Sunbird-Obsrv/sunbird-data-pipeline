# Unique Signup

## Single Session
* I have played only once
* There should be "1" GE_SESSION_START event
* There should be "1" GE_SIGNUP event

## Multiple Session on same day
* I have played only once
* I play "3" times on the same day
* There should be "4" GE_SESSION_START event
* There should be "1" GE_SIGNUP event

## Multiple Session on different days
* I have played only once
* I play "3" times on the same day
* I play "4" times the next day
* There should be "8" GE_SESSION_START event
* There should be "1" GE_SIGNUP event
