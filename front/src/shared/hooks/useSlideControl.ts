import { useState } from 'react';

export const useSlideControl = <T>(items: T[], itemsPerPage: number) => {
    const [currentPage, setCurrentPage] = useState(0);
    const maxPages = Math.ceil(items.length / itemsPerPage) - 1;

    const getVisibleItems = () => items.slice(
        currentPage * itemsPerPage,
        (currentPage + 1) * itemsPerPage
    );

    return {
        currentPage,
        maxPages,
        visibleItems: getVisibleItems(),
        handlePrevClick: () => setCurrentPage(Math.max(0, currentPage - 1)),
        handleNextClick: () => setCurrentPage(Math.min(maxPages, currentPage + 1))
    };
};