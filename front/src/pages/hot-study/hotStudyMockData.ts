export interface StudyCard {
    deadline: string;
    title: string;
    viewCount: number;
    tags: string[];
}

export const studyCardsData: StudyCard[] = [
    {
        deadline: '2025.03.15',
        title: 'AWS 클라우드 아키텍처 설계 스터디',
        viewCount: 2547,
        tags: ['AWS']
    },
    {
        deadline: '2025.03.15',
        title: 'Spring Boot 마이크로서비스 구축하기',
        viewCount: 1876,
        tags: ['소프트웨어 개발']
    },
    {
        deadline: '2025.03.01',
        title: 'Linux 시스템 관리자 되기',
        viewCount: 1355,
        tags: ['Linux']
    },
    {
        deadline: '2025.03.22',
        title: 'Azure DevOps 실전 프로젝트',
        viewCount: 982,
        tags: ['Azure']
    },
    {
        deadline: '2025.03.20',
        title: 'React Native 모바일 앱 개발',
        viewCount: 1234,
        tags: ['React']
    },
    {
        deadline: '2025.03.25',
        title: 'Docker & Kubernetes 실무 적용',
        viewCount: 1567,
        tags: ['DevOps']
    },
    {
        deadline: '2025.03.18',
        title: 'Python 데이터 사이언스',
        viewCount: 2145,
        tags: ['Python']
    },
    {
        deadline: '2025.03.30',
        title: 'Vue.js 프론트엔드 마스터',
        viewCount: 891,
        tags: ['Vue.js']
    }
];