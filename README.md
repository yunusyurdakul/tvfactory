Factory service has 3 endpoints

# GET: api/factory/start
  Starts the factory to play the game. If there is a continuing game, it won't start a new one. After a finished game we can start again.
  
```
{
  "message": "Factory is started!"
}
```

# GET: api/factory/result
  Shows current status of the factory.
  
```
{
  "data": {
    "balance": 10,
    "televisionCount": 3,
    "robotCount": 8,
    "panelCount": 2,
    "mainBoardCount": 5
  },
  "message": "Factory is still running!"
}
```

# GET: api/factory/reset
  Resets the results.
