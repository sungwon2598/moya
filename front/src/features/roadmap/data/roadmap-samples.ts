import { Code, Server, Smartphone, Binary, Database } from 'lucide-react';
import { RoadmapItem } from '../../../core/types/roadmap.ts';

export const roadmapSamples: RoadmapItem[] = [
    {
        title: "Frontend",
        icon: Code,
        keywords: ["HTML", "CSS", "JavaScript", "React"],
        color: "blue"
    },
    {
        title: "Backend",
        icon: Server,
        keywords: ["Java", "Spring", "Node.js", "Database"],
        color: "green"
    },
    {
        title: "Mobile",
        icon: Smartphone,
        keywords: ["Android", "iOS", "Flutter", "React Native"],
        color: "purple"
    },
    {
        title: "알고리즘",
        icon: Binary,
        keywords: ["자료구조", "알고리즘", "문제해결", "코딩테스트"],
        color: "yellow"
    },
    {
        title: "CS",
        icon: Database,
        keywords: ["운영체제", "네트워크", "데이터베이스", "컴퓨터구조"],
        color: "red"
    }
];
