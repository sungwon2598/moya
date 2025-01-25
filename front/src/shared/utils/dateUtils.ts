export const calculateDaysLeft = (deadline: string): number => {
    const diff = new Date(deadline).getTime() - new Date().getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
};