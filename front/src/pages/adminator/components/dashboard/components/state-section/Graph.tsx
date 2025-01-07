import React from 'react';
import { Sparklines, SparklinesBars } from 'react-sparklines';

interface GraphProps {
    data: number[];
    color: string;
    width?: number;    // width prop 추가
    height?: number;   // height prop 추가
}

const Graph: React.FC<GraphProps> = ({
                                         data,
                                         color,
                                         width = 50,      // 원본과 동일한 기본값
                                         height = 20       // 원본과 동일한 기본값
                                     }) => {
    return (
        <div style={{ width: width, display: 'inline-block' }}>
            <Sparklines
                data={data}
                width={width}
                height={height}
                margin={2}
                style={{ maxWidth: '100%' }}
            >
                <SparklinesBars
                    style={{
                        stroke: 'none',
                        fill: color
                    }}
                />
            </Sparklines>
        </div>
    );
};

export default Graph;