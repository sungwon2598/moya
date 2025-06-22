import { useEffect, useRef, useState } from 'react';
import Matter from 'matter-js';

const keywords = [
  '🎨 Art',
  '🎮 Gaming',
  '🎵 Music',
  '📚 Reading',
  '📸 Photography',
  '🏃 Exercise',
  '🎬 Movies',
  '🧘 Yoga',
  '✈️ Travel',
  '🧩 Puzzle',
  '💻 Programming',
  '🍳 Cooking',
  '🎯 Goals',
  '📝 Writing',
  '🔬 Science',
];

type KeywordBody = Matter.Body & {
  labelText: string;
  width: number;
  height: number;
  fontSize: number;
};

export default function FallingKeywords() {
  const sceneRef = useRef<HTMLDivElement>(null);
  const engineRef = useRef<Matter.Engine | null>(null);
  const renderRef = useRef<Matter.Render | null>(null);
  const runnerRef = useRef<Matter.Runner | null>(null);
  const [bodies, setBodies] = useState<KeywordBody[]>([]);
  const [dimensions, setDimensions] = useState({
    width: window.innerWidth,
    height: window.innerHeight,
  });

  // 마우스 드래그 활성화 여부
  const enableDrag = false;

  // 텍스트 폭 캐시
  const textWidthCache = useRef<Map<string, number>>(new Map());

  useEffect(() => {
    if (!sceneRef.current) return;
    const observer = new ResizeObserver((entries) => {
      for (const entry of entries) {
        const { width, height } = entry.contentRect;
        setDimensions({ width, height });
      }
    });
    observer.observe(sceneRef.current);
    return () => observer.disconnect();
  }, []);

  // 텍스트 폭 측정 및 캐시
  const measureTextWidth = (text: string, fontSize: number) => {
    const key = `${text}-${fontSize}`;
    if (textWidthCache.current.has(key)) {
      return textWidthCache.current.get(key)!;
    }
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d')!;
    ctx.font = `${fontSize}px Pretendard, sans-serif`;
    const width = ctx.measureText(text).width;
    textWidthCache.current.set(key, width);
    return width;
  };

  useEffect(() => {
    if (dimensions.width <= 0 || dimensions.height <= 0) return;

    if (engineRef.current) {
      Matter.Render.stop(renderRef.current!);
      renderRef.current?.canvas?.remove();
      Matter.Runner.stop(runnerRef.current!);
      Matter.Engine.clear(engineRef.current);
    }

    const { Engine, Render, Runner, World, Bodies, Mouse, MouseConstraint } = Matter;

    const engine = Engine.create();
    engineRef.current = engine;
    engine.world.gravity.y = 0.8;

    const { width, height } = dimensions;

    const render = Render.create({
      element: sceneRef.current!,
      engine,
      options: {
        width,
        height,
        wireframes: false,
        background: 'transparent',
      },
    });
    renderRef.current = render;

    // 벽 설정 (화면 밖 충돌 방지)
    const ground = Bodies.rectangle(width / 2, height - 10, width, 100, {
      isStatic: true,
      render: { visible: false },
    });
    const leftWall = Bodies.rectangle(-50, height / 2, 100, height, {
      isStatic: true,
      render: { visible: false },
    });
    const rightWall = Bodies.rectangle(width + 50, height / 2, 100, height, {
      isStatic: true,
      render: { visible: false },
    });
    World.add(engine.world, [ground, leftWall, rightWall]);

    const maxKeywordCount = dimensions.width < 600 ? 8 : keywords.length;

    const keywordBodies: KeywordBody[] = keywords.slice(0, maxKeywordCount).map((text, index) => {
      const fontSize = dimensions.width < 600 ? 12 : Math.random() * 8 + 12;
      const padding = Math.random() * 8 + 16;
      const textWidth = measureTextWidth(text, fontSize);
      const bodyWidth = textWidth + padding * 2;
      const bodyHeight = fontSize + padding;
      const x = Math.random() * (width - bodyWidth) + bodyWidth / 2;
      const y = -100 - index * 80 - Math.random() * 200;

      const body = Bodies.rectangle(x, y, bodyWidth, bodyHeight, {
        restitution: 0.6,
        friction: 0.2,
        frictionAir: 0.01,
        render: { visible: false },
      }) as KeywordBody;

      body.labelText = text;
      body.width = bodyWidth;
      body.height = bodyHeight;
      body.fontSize = fontSize;

      return body;
    });

    World.add(engine.world, keywordBodies);
    setBodies(keywordBodies);

    if (enableDrag) {
      const mouse = Mouse.create(render.canvas);
      const mouseConstraint = MouseConstraint.create(engine, {
        mouse,
        constraint: {
          stiffness: 0.2,
          render: { visible: false },
        },
      });
      World.add(engine.world, mouseConstraint);
      render.canvas.style.pointerEvents = 'auto';
    } else {
      render.canvas.style.pointerEvents = 'none';
    }

    Engine.run(engine);
    Render.run(render);
    const runner = Runner.create();
    runnerRef.current = runner;
    Runner.run(runner, engine);

    // 키워드 주기적 추가 (최대 30개 제한)
    const addNewKeyword = () => {
      if (!engineRef.current) return;
      const randomKeyword = keywords[Math.floor(Math.random() * keywords.length)];
      const fontSize = Math.random() * 8 + 12;
      const padding = Math.random() * 8 + 16;
      const textWidth = measureTextWidth(randomKeyword, fontSize);
      const bodyWidth = textWidth + padding * 2;
      const bodyHeight = fontSize + padding;
      const x = Math.random() * (width - bodyWidth) + bodyWidth / 2;
      const y = -100;

      const newBody = Bodies.rectangle(x, y, bodyWidth, bodyHeight, {
        restitution: 0.6,
        friction: 0.2,
        frictionAir: 0.01,
        render: { visible: false },
      }) as KeywordBody;

      newBody.labelText = randomKeyword;
      newBody.width = bodyWidth;
      newBody.height = bodyHeight;
      newBody.fontSize = fontSize;

      World.add(engine.world, newBody);

      setBodies((prev) => {
        const next = [...prev, newBody];
        // 최대 30개만 유지
        if (next.length > 30) {
          // 엔진에서 오래된 바디 제거
          const toRemove = next.slice(0, next.length - 30);
          toRemove.forEach((body) => {
            World.remove(engine.world, body);
          });
          return next.slice(next.length - 30);
        }
        return next;
      });
    };

    const keywordInterval = setInterval(addNewKeyword, 5000);

    return () => {
      clearInterval(keywordInterval);
      render.canvas.remove();
      Render.stop(render);
      Runner.stop(runner);
      Engine.clear(engine);
    };
  }, [dimensions]);

  // 화면 밖으로 떨어진 바디 정기적으로 제거 (3초마다)
  useEffect(() => {
    if (!engineRef.current) return;
    const cleanup = () => {
      setBodies((prev) => {
        const filtered = prev.filter((body) => body.position.y < dimensions.height + 200);
        // 물리엔진에서도 제거
        const toRemove = prev.filter((body) => body.position.y >= dimensions.height + 200);
        toRemove.forEach((body) => {
          Matter.World.remove(engineRef.current!.world, body);
        });
        return filtered;
      });
    };
    const interval = setInterval(cleanup, 3000);
    return () => clearInterval(interval);
  }, [dimensions]);

  // 위치 동기화용 리렌더 주기 33ms (약 30FPS)
  const [, forceUpdate] = useState(0);
  useEffect(() => {
    const interval = setInterval(() => forceUpdate((n) => n + 1), 33);
    return () => clearInterval(interval);
  }, []);

  return (
    <div
      ref={sceneRef}
      className="pointer-events-none absolute left-0 top-0 z-[-1] mx-auto h-full w-full overflow-hidden">
      {bodies.map((body, index) => {
        if (body.position.y > dimensions.height + 100) return null;
        return (
          <div
            key={`${body.labelText}-${index}`}
            className="absolute flex items-center justify-center rounded-full font-semibold text-white shadow-[0_4px_20px_rgba(0,0,0,0.2)] transition-opacity duration-300"
            style={{
              transform: `translate(${body.position.x - body.width / 2}px, ${body.position.y - body.height / 2}px) rotate(${body.angle}rad)`,
              width: `${body.width}px`,
              height: `${body.height}px`,
              fontSize: `${body.fontSize}px`,
              background: 'rgba(255, 255, 255, 0.08)',
              backdropFilter: 'blur(10px)',
              WebkitBackdropFilter: 'blur(10px)',
              borderRadius: '9999px',
              border: '1px solid',
              pointerEvents: 'none',
              opacity: body.position.y < -50 ? 0 : 1,
              zIndex: 10,
            }}>
            {body.labelText}
          </div>
        );
      })}
    </div>
  );
}
