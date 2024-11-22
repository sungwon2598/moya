import { ChevronRight } from 'lucide-react';
import { FC } from 'react';
import { RoadmapItem } from '../../types/roadmap';

export const RoadmapCard: FC<RoadmapItem> = ({ title, icon: Icon, keywords, color }) => {
    const getColorClasses = (color: string) => {
        const colors = {
            blue: { bg: "bg-blue-100", text: "text-blue-600", hover: "hover:bg-blue-50" },
            green: { bg: "bg-green-100", text: "text-green-600", hover: "hover:bg-green-50" },
            purple: { bg: "bg-purple-100", text: "text-purple-600", hover: "hover:bg-purple-50" },
            yellow: { bg: "bg-yellow-100", text: "text-yellow-600", hover: "hover:bg-yellow-50" },
            red: { bg: "bg-red-100", text: "text-red-600", hover: "hover:bg-red-50" }
        };
        return colors[color as keyof typeof colors];
    };

    const colors = getColorClasses(color);

    return (
        <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-all duration-300 cursor-pointer group">
            <div className="flex justify-between items-center mb-4">
                <div className="flex items-center gap-3">
                    <div className={`${colors.bg} p-2 rounded-lg ${colors.text} group-hover:scale-110 transition-transform`}>
                        <Icon size={24} />
                    </div>
                    <h2 className="text-lg font-semibold">{title}</h2>
                </div>
                <ChevronRight className={`w-5 h-5 ${colors.text} group-hover:translate-x-1 transition-transform`} />
            </div>
            <div className={`w-full h-40 ${colors.bg} rounded-md mb-4 p-4 ${colors.hover} transition-colors`}>
                <div className="flex flex-wrap gap-2">
                    {keywords.map((keyword, kidx) => (
                        <span key={kidx} className={`${colors.text} bg-white px-3 py-1 rounded-full text-sm font-medium`}>
                            {keyword}
                        </span>
                    ))}
                </div>
            </div>
        </div>
    );
};