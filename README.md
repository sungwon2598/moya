# moya


## 기술 스택

| 분야 | 기술 스택 |
|:---|:---|
| 백엔드 | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat-square&logo=spring-boot) ![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=java) |
| 데이터베이스 | ![MySQL](https://img.shields.io/badge/MySQL-8.0.35-4479A1?style=flat-square&logo=mysql) ![Redis](https://img.shields.io/badge/Redis-Latest-DC382D?style=flat-square&logo=redis) |
| ORM | ![JPA](https://img.shields.io/badge/JPA-Latest-59666C?style=flat-square&logo=hibernate&logoColor=white) ![QueryDSL](https://img.shields.io/badge/QueryDSL-Latest-0769AD?style=flat-square) |
| 보안 | ![Spring Security](https://img.shields.io/badge/Spring%20Security-6.3.1-6DB33F?style=flat-square&logo=spring-security) |
| 클라우드 | ![AWS](https://img.shields.io/badge/AWS-Cloud-232F3E?style=flat-square&logo=amazon-aws) |
| CI/CD | ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-2088FF?style=flat-square&logo=github-actions) |
| 협업 툴 | ![Confluence](https://img.shields.io/badge/Confluence-172B4D?style=flat-square&logo=confluence) ![Jira](https://img.shields.io/badge/Jira-0052CC?style=flat-square&logo=jira) ![Slack](https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack) |
| API 문서화 | ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black) |




## 개발서버 아키텍처

```mermaid
flowchart TB
    subgraph GitHub ["GitHub"]
        direction TB
        GH_BE[백엔드 레포지토리]
        GH_FE[프론트엔드 레포지토리]
        GH_ACTION_BE[GitHub Actions\n백엔드]
        GH_ACTION_FE[GitHub Actions\n프론트엔드]

        GH_BE --> GH_ACTION_BE
        GH_FE --> GH_ACTION_FE
    end
    subgraph AWS ["AWS"]
        direction TB
        subgraph Compute ["Compute & Storage"]
            subgraph EC2 [EC2]
                SPRING[Spring Boot]
                SWAGGER[Swagger]
                SPRING --- SWAGGER
            end
            S3[S3\nReact]
        end

        subgraph Database ["Database"]
            RDS[(RDS\nMySQL)]
            REDIS[(ElastiCache\nRedis)]
        end

        CW[CloudWatch]
    end
    SLACK[Slack]
    GH_BE & GH_FE --> |변경/커밋/PR\n알림| SLACK
    GH_ACTION_BE --> |배포| EC2
    GH_ACTION_FE --> |배포| S3
    EC2 <--> RDS & REDIS
    CW --> |알림| SLACK
    CW -.-> |모니터링| EC2 & RDS & S3
    classDef awsColor fill:#FF9900,stroke:#232F3E,color:#232F3E;
    classDef githubColor fill:#24292E,stroke:#000000,color:#FFFFFF;
    classDef slackColor fill:#4A154B,stroke:#000000,color:#FFFFFF;
    classDef swaggerColor fill:#85EA2D,stroke:#173647,color:#173647;

    class EC2,S3,RDS,REDIS,CW awsColor;
    class GH_BE,GH_FE,GH_ACTION_BE,GH_ACTION_FE githubColor;
    class SLACK slackColor;
    class SWAGGER swaggerColor;
