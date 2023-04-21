# Metaheuristic behavior platform

Metaheuristic behavior platform (MHBP) is an application for continuous evaluation and assessing LLM-base APIs.  


## Table of contents

- [Quick start](#quick-start)
- [License](#license)
- [Copyright](#copyright)
- [How to cite](#how-to-cite)

## Quick start

##### Quick start for evaluating UI only

1. Create a temporary dir for MHBP, i.e. /mhbp-home
   It'll be /mhbp-home in following text.

1. Set system environment variable MHBP_HOME to full path of /mhbp-home, i.e. $MHBP_HOME or %MHBP_HOME% depending on your OS

1. Clone git repository:
```text
git clone https://github.com/sergmain/mhbp.git
```

1. run command:
```text
mvnw clean package -f pom.xml -Dmaven.test.skip=true
```

1. run command:
```text
java -jar target/mhbp.jar
```

1. Access MHBP at https://amhbp8080.metaheuristic.ai
   login - q, password - 123

1. Press 'Dispatcher' at the top menu.  

2. Press 'Company' button.

3. Create a new company 'My company'  

1. Go to 'Accounts' and create a new account, after creating an account select roles ROLE_MANAGER, ROLE_ADMIN. 

1. Re-log in with created account

2. Explore the interface or continue with documentation at [TODO]



## License and licensing
MHBP is licensed under Apache 2.0 type license.

## Copyright
Innovation platforms LLC, San Francisco, US, 2023 


## How to cite

Please cite this repository if it was useful for your research:

The bibtex entry for this is:
```text
@misc{metaheuristic,
  author = {Lissner, Sergio},
  title = {Metaheuristic behavior platform},
  year = {2023},
  publisher = {GitHub},
  journal = {GitHub repository},
  howpublished = {\url{https://github.com/sergmain/metaheuristic-mhbp}},
}
```

