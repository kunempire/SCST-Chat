Here is the Register Server for SCST Chat developed by JavaSE-17. Let's have a quick overview.

```bash
.
├── lib
├── README.md # Here you are
├── src
│   ├── main
│   │   ├── java
│   │   │   └── SCSTChat
│   │   │       ├── main
│   │   │       │   ├── Assistant.java # assitant thread
│   │   │       │   ├── Main.java
│   │   │       │   └── RegisterServer.java # main thread
│   │   │       ├── register # protocol
│   │   │       │   ├── RequestForServer.java
│   │   │       │   └── ResponseFromServer.java
│   │   │       └── utils
│   │   │           └── SQL.java # !!need to modify!!
│   │   └── resources
│   │       └── mysql-connector-j-8.1.0.jar
│   └── test
└── .vscode # an example for config in VScode IDE
    └── settings.json

```

The database here is mysql-server.