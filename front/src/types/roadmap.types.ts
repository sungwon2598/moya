export type Day = {
  day: number;
  dailyKeyword: string;
  worksheet: string;
};

export type Week = {
  week: number;
  weeklyKeyword: string;
  dailyPlans: Day[];
};
export type RoadmapData = {
  curriculumEvaluation: string;
  overallTips: string[];
  weeklyPlans: Week[];
  hasRestrictedTopics?: string;
};
