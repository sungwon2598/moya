import { Week } from '@/types/roadmap.types';
import { useSprings, animated, to as interpolate } from '@react-spring/web';
import { useState } from 'react';
import { useDrag } from '@use-gesture/react';
import { Button } from '@/components/shared/ui';

interface CardProps {
  weeks: Week[];
}

export default function WeeklyPlanCard({ weeks }: CardProps) {
  const to = (i: number) => ({
    x: 0,
    y: i * -4,
    scale: 1,
    rot: -10 + Math.random() * 20,
    delay: i * 100,
  });
  const from = () => ({ x: 0, rot: 0, scale: 1.5, y: -1000 });
  const trans = (r: number, s: number) => `perspective(1500px) rotateZ(${r}deg) scale(${s})`;

  const [gone] = useState(() => new Set()); // The set flags all the cards that are flicked out
  const [props, api] = useSprings(weeks.length, (i) => ({
    ...to(i),
    from: from(),
  }));

  const bind = useDrag(({ args: [index], down, movement: [mx], direction: [xDir], velocity }) => {
    const trigger = Math.abs(mx) > 50 || Number(velocity) > 0.2;
    const dir = xDir < 0 ? -1 : 1;
    if (!down && trigger) gone.add(index);
    api.start((i) => {
      if (index !== i) return;
      const isGone = gone.has(index);
      const x = isGone ? (200 + window.innerWidth) * dir : down ? mx : 0;
      const rot = Number(mx) / 100 + (isGone ? dir * 10 * Number(velocity) : 0); // How much the card tilts, flicking it harder makes it rotate faster
      const scale = down ? 1.1 : 1; // Active cards lift up a bit
      return {
        x,
        rot,
        scale,
        delay: undefined,
        config: { friction: 50, tension: down ? 800 : isGone ? 200 : 500 },
      };
    });
    if (!down && gone.size === weeks.length)
      setTimeout(() => {
        gone.clear();
        api.start((i) => to(i));
      }, 300);
  });

  //다시보기
  const resetCards = () => {
    gone.clear();
    api.start((i) => to(i));
  };
  //더블클릭했을때
  const HandlerCardDoubleClick = (i: number) => {
    gone.add(i);
    api.start((j) => {
      if (i !== j) return;
      const dir = 1;
      const velocity = 1;
      return {
        x: (300 + window.innerWidth) * dir,
        rot: dir * 10 * velocity,
        scale: 1,
        delay: undefined,
        config: { friction: 50, tension: 200 },
      };
    });
    if (gone.size === weeks.length) {
      setTimeout(() => {
        resetCards();
      }, 600);
    }
  };

  return (
    <div className="relative flex h-screen items-center justify-center">
      {props.map(({ x, y, rot, scale }, i) => {
        const isGone = gone.has(i);
        return (
          <animated.div
            key={i}
            style={{
              zIndex: weeks.length - i,
              x,
              y,
            }}
            className={'absolute top-12 w-3/5'}>
            <animated.div
              {...bind(i)}
              onDoubleClick={() => HandlerCardDoubleClick(i)}
              style={{
                transform: interpolate([rot, scale], trans),
                display: isGone ? 'none' : 'block',
                touchAction: 'none',
              }}
              className="w-full cursor-grab select-none rounded-xl border bg-white p-6 px-3 pt-4 shadow-lg">
              <div className="border-b">
                <h6 className="text-moya-primary">
                  <span className="text-sm">{weeks[i].week}주차</span>
                </h6>
                <h5 className="py-2">
                  <span className="text-2xl">{weeks[i].weeklyKeyword}</span>
                </h5>
                {weeks[i].dailyPlans.map((day, index) => (
                  <p key={day.day} className={`flex ${index < weeks[i].dailyPlans.length - 1 ? 'border-b' : ''}`}>
                    <span className="mr-2 inline-block basis-1/5 border-r py-1 text-center text-neutral-700">
                      {day.day}일차
                    </span>
                    <span className="basis-4/5 py-1">{day.dailyKeyword}</span>
                  </p>
                ))}
              </div>
            </animated.div>
          </animated.div>
        );
      })}
      <div className="mt-60 text-center">
        <p className="p-3 text-sm text-neutral-600">더블클릭하거나 드레그 하면 다음 주차 내용이 보여요.</p>
        <Button onClick={resetCards}>1주차 다시 보기</Button>
      </div>
    </div>
  );
}
