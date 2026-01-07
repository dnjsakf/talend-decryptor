TOS 7.3.1 기준

TOS를 최초 실행 할 때, Connection을 설정해야 하는데 설정하면, `configuration/connection_user.properties` 파일이 생성됨.
해당 파일에는 workspace의 경로가 설정되어 있는데, 사용자 workspace의 경로와 다르면 수동으로 셋팅해줘야함.
다만, 해당 값이 암호화되어 있어서 쉽게  변경할 수 없으므로 해당 코드를 사용하여 설정 파일을 생성하여 사용.

```bash
Usage: java -Duser.dir=<OutputDir> -jar talend-decryptor.jar <WorkspaceDir> [<ConnectionName>]
```
