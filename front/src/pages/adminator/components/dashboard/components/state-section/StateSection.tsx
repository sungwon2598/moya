import React from 'react';
import StatCard from './StatCard';
import "@pages/adminator/assets/styles/index.scss"

const StateSection: React.FC = () => {
    const stats = [
        {
            title: 'Total Visits',
            data: [0, 5, 6, 10, 9, 12, 4, 9],
            color: '#4caf50',
            percentage: '+10%',
            percentageBgColor: 'bgc-green-50',
            percentageTextColor: 'c-green-500',
        },
        {
            title: 'Total Page Views',
            data: [0, 5, 6, 10, 9, 12, 4, 9],
            color: '#9675ce',
            percentage: '-7%',
            percentageBgColor: 'bgc-red-50',
            percentageTextColor: 'c-red-500',
        },
        {
            title: 'Unique Visitor',
            data: [0, 5, 6, 10, 9, 12, 4, 9],
            color: '#03a9f3',
            percentage: '~12%',
            percentageBgColor: 'bgc-purple-50',
            percentageTextColor: 'c-purple-500',
        },
        {
            title: 'Bounce Rate',
            data: [0, 5, 6, 10, 9, 12, 4, 9],
            color: '#f96262',
            percentage: '33%',
            percentageBgColor: 'bgc-blue-50',
            percentageTextColor: 'c-blue-500',
        },
    ];

    return (
        // <div className="row gap-20">
        <div className="row custom-gap-20">
                {stats.map((stat, index) => (
                    <StatCard
                        key={index}
                        title={stat.title}
                        data={stat.data}
                        color={stat.color}
                        percentage={stat.percentage}
                        percentageBgColor={stat.percentageBgColor}
                        percentageTextColor={stat.percentageTextColor}
                    />
                ))}
        </div>
            );
            };

            export default StateSection;