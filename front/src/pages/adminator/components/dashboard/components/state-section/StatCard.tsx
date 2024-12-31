import React from 'react';
import Graph from './Graph';
import "@pages/adminator/assets/styles/index.scss"

interface StatCardProps {
    title: string;
    data: number[];
    color: string;
    percentage: string;
    percentageBgColor: string;
    percentageTextColor: string;
}

const StatCard: React.FC<StatCardProps> = ({
                                               title,
                                               data,
                                               color,
                                               percentage,
                                               percentageBgColor,
                                               percentageTextColor,
                                           }) => {
    return (
        <div className="col-md-3">
            <div className="layers bd bgc-white p-20">
                {/* 제목 섹션 */}
                <div className="layer w-100 mB-10">
                    <h6 className="lh-1">{title}</h6>
                </div>

                {/* 그래프 및 퍼센트 섹션 */}
                <div className="layer w-100">
                    <div className="peers ai-sb fxw-nw">
                        {/* 그래프 영역 */}
                        <div className="peer peer-greed">
                            <Graph data={data} color={color} />
                        </div>
                        {/* 퍼센트 정보 */}
                        <div className="peer">
                            <span
                                className={`d-ib lh-0 va-m fw-600 bdrs-10em pX-15 pY-15 ${percentageBgColor} ${percentageTextColor}`}
                            >
                                {percentage}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default StatCard;