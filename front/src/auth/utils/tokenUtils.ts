export const setStoredToken = (token: string) => {
    localStorage.setItem('token', token);
};

export const getStoredToken = () => {
    return localStorage.getItem('token');
};

export const removeStoredToken = () => {
    localStorage.removeItem('token');
};
