export const calculateDaysLeft = (deadline: string): number => {
  const diff = new Date(deadline).getTime() - new Date().getTime();
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
};

// 음수이면 마감된 스터디, 양수이면 아직 진행 중인 스터디
