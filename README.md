# Metaheuristic behavior platform

Metaheuristic behavior platform (MHBP) is an application for continuous evaluation and assessing LLM-base APIs.  

# Everything in this repository was moved to the main repository of Metaheuristic - https://github.com/sergmain/metaheuristic

## Table of contents

- [Quick start](#quick-start)
- [License](#license)
- [Copyright](#copyright)
- [How to cite](#how-to-cite)

## Quick start


## Prerequisites:
#### Java Development Kit (JDK) 17

To run MHBP you have to have jdk 17 installed \
Right now, there isn't any known bug which restricts to use certain JDK.

[AdoptiumJDK (AKA OpenJDK) 17](https://adoptium.net/releases.html?variant=openjdk17&jvmVariant=hotspot) \
[Zulu JDK 17](https://www.azul.com/downloads/?version=java-17-lts)

If java was already installed, you can check the current version with command:
```text
java --version
```  

#### Git
Right now, installed git is required. You can check git with
```text
git --version
```  

## Steps

1. Create a temporary dir for MHBP, i.e. /mhbp-home \
   It'll be /mhbp-home in following text.

   ```text
   git clone https://github.com/sergmain/mhbp.git
   ```

   ```text
   mvnw clean package -f pom.xml -Dmaven.test.skip=true
   ```

   ```text
   java -jar -Xms1g -Xmx1g -Dfile.encoding=UTF-8 -Dmhbp.home=/mhbp_home target/mhbp.jar
   ```
   > **-Dmhbp.home=/mhbp_home** must point to full path of dir, created in step 1.

1. Access MHBP at https://amhbp8080.metaheuristic.ai \
   login - q \
   password - 123

1. Press 'Dispatcher' at the top menu.  

2. Press 'Company' button.

3. Create a new company 'My company'  

1. Go to 'Accounts' and create a new account, after creating an account select roles ROLE_MANAGER, ROLE_ADMIN. 

1. Re-log in with created account

2. Explore the interface or continue with documentation at https://github.com/sergmain/mhbp/wiki/Table-of-content



## License and licensing
MHBP is licensed under Apache 2.0 type license.

## Copyright
Innovation platforms LLC, San Francisco, US, 2023 


## How to cite

Please cite this repository if it was useful in your research:

The BibTeX entry for this is:
```text
@misc{Metaheuristic behavior platform,
  author = {Lissner, Sergio},
  title = {Metaheuristic behavior platform},
  year = {2023},
  publisher = {GitHub},
  journal = {GitHub repository},
  howpublished = {\url{https://github.com/sergmain/mhbp}},
}
```

