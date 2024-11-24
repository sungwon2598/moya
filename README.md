# moya


## 기술 스택


| 분야 | 기술 스택 |
|:---|:---|
| 백엔드 | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat-square&logo=spring-boot) ![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=java) |
| 데이터베이스 | ![MySQL](https://img.shields.io/badge/MySQL-8.0.35-4479A1?style=flat-square&logo=mysql) ![Redis](https://img.shields.io/badge/Redis-Latest-DC382D?style=flat-square&logo=redis) |
| ORM | ![JPA](https://img.shields.io/badge/JPA-Latest-59666C?style=flat-square&logo=hibernate&logoColor=white) ![QueryDSL](https://img.shields.io/badge/QueryDSL-Latest-0769AD?style=flat-square) |
| 보안 | ![Spring Security](https://img.shields.io/badge/Spring%20Security-6.3.1-6DB33F?style=flat-square&logo=spring-security) |
| 인프라 | ![Docker](https://img.shields.io/badge/Docker-Latest-2496ED?style=flat-square&logo=docker) ![Ubuntu](https://img.shields.io/badge/Ubuntu-Server-E95420?style=flat-square&logo=ubuntu) ![Nginx](https://img.shields.io/badge/Nginx-Latest-009639?style=flat-square&logo=nginx) |
| 모니터링 | ![Prometheus](https://img.shields.io/badge/Prometheus-Latest-E6522C?style=flat-square&logo=prometheus) ![Grafana](https://img.shields.io/badge/Grafana-Latest-F46800?style=flat-square&logo=grafana) |
| 테스트 | ![JUnit](https://img.shields.io/badge/JUnit-5-25A162?style=flat-square&logo=junit5) ![JMeter](https://img.shields.io/badge/JMeter-Latest-D22128?style=flat-square&logo=apache) ![Mockito](https://img.shields.io/badge/Mockito-Latest-25A162?style=flat-square) |
| 백업 | ![AWS](https://img.shields.io/badge/AWS-Backup-232F3E?style=flat-square&logo=amazon-aws) |
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
```

## 개발서버 아키텍처(최신)

```mermaid
flowchart LR
    subgraph Internet["인터넷"]
        D[가비아 도메인]
        DDNS[Duck DDNS]
        GH[GitHub]
        
        subgraph AWS["AWS"]
            S3_BACKUP[(S3 백업)]
            S3_FRONT[(S3 프론트엔드)]
            CF[CloudFront]
        end
    end
    
    subgraph Network["네트워크 장비"]
        NS[NAT 스위치]
        R[공유기]
    end
    
    subgraph HomeServer["홈서버"]
        direction TB
        
        subgraph Docker["Docker Environment"]
            NGINX[엔진엑스]
            
            subgraph APIContainer["API 컨테이너"]
                SB[스프링부트]
                SW[Swagger UI]
                SB --- SW
            end
            
            subgraph DBContainer["Database 컨테이너"]
                RD[(Redis)]
                MS[(MySQL)]
            end
            
            subgraph Monitoring["모니터링"]
                PR[Prometheus]
                GF[Grafana]
                LK[Loki]
                
                PR --> GF
                LK --> GF
            end
            
            NGINX --> SB
            SB --> RD
            SB --> MS
            
            SB -- 메트릭 수집 --> PR
            SB -- 로그 수집 --> LK
            MS -- 메트릭 수집 --> PR
            RD -- 메트릭 수집 --> PR
            NGINX -- 로그 수집 --> LK
        end
        
        IP[IP 감지 스크립트]
        BACKUP[백업 스크립트]
        
        IP -. 5분마다 외부 IP 업데이트 .-> DDNS
        BACKUP -. 자동 백업 .-> S3_BACKUP
    end
    
    D --> CF
    D --> DDNS
    DDNS --> NS
    NS --> R
    R --> NGINX
    
    GH --> |Backend CI/CD| APIContainer
    GH --> |Frontend CI/CD| S3_FRONT
    
    S3_FRONT --> CF
    
    RD -. 백업 .-> BACKUP
    MS -. 백업 .-> BACKUP
    
    style HomeServer fill:#f5f5f5,stroke:#333,stroke-width:2px
    style Internet fill:#e6f3ff,stroke:#333,stroke-width:2px
    style Network fill:#fff5e6,stroke:#333,stroke-width:2px
    style Docker fill:#e6ffef,stroke:#333,stroke-width:2px
    style AWS fill:#fce8d6,stroke:#333,stroke-width:2px
    style Monitoring fill:#f0e6ff,stroke:#333,stroke-width:2px
    
    classDef database fill:#f9f,stroke:#333,stroke-width:2px
    class RD,MS,S3_BACKUP,S3_FRONT database
    
    classDef container fill:#e6fff2,stroke:#333,stroke-width:2px
    class APIContainer,DBContainer container
    
    classDef monitoring fill:#f0e6ff,stroke:#333,stroke-width:2px
    class PR,GF,LK monitoring


    



