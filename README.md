# offline
오프라인 문제에 대한 풀이 코드를 업로드하는 레포지토리


## 사용 방식

1. 스터디 시작 전에 로컬 레포지토리를 최신화
```sh
  git pull
```

2. 각자의 브랜치 생성
```sh
  git checkout -b 브랜치명
```

3. 스터디 일자 디렉토리의 problem.md를 확인하기
  
4. 자기 이름으로 된 디렉토리 생성

5. 문제 번호.java 형식으로 문제 해결한 코드 파일만 업로드

6. 끝나면 pull request 보내기 (merge는 팀장이 함)
```sh
  git add *.java
  git commit -m "~~"
  git push -u origin 자기브랜치명
  // 깃헙으로 접속해서 pull request 생성
```

## problem.md
그 주의 문제 담당자가 문제를 2~3개 선정해서 문제 제목 + 링크를 작성해서 업로드하기

## 디렉토리 구조 예시
```
📂 root
├── 📄 Readme.md
├── 📂 day_0213
│   ├── 📂 sangchan
│   └── 📄 problem.md
└── 📂 day_0214
    ├── 📂 sangchan
    └── 📄 problem.md
```
