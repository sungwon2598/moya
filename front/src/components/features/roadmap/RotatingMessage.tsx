import { useRotatingMessage } from '@/features/roadmap/hooks/useRotatingMessage';
import { RoadmapData } from '@/types/roadmap.types';

interface RotatingMessageType {
  data: RoadmapData;
}

export default function RotatingMessage({ data }: RotatingMessageType) {
  const { currentMessage } = useRotatingMessage(data && data?.overallTips?.length > 0 ? data.overallTips : [''], 5000);

  return (
    <div className="group relative z-10 pb-2">
      <div className="rounded-lg bg-neutral-100 p-2 text-black">
        <p className="mb-1 font-semibold">📚 Tip.</p>
        <p>{currentMessage as string}</p>
      </div>

      <div className="absolute bottom-full left-0 mb-2 hidden w-full rounded-lg border border-neutral-400 bg-neutral-200 p-2 shadow-2xl group-hover:block">
        {data.overallTips.map((tip: string, index: number) => (
          <p
            key={index}
            className={`${currentMessage === tip ? 'text-moya-primary font-semibold' : 'text-neutral-400'}`}>
            {tip}
          </p>
        ))}
      </div>
    </div>
  );
}
