import { FC, useState } from 'react';
import developmentImg from '@/assets/development.jpg';

const hobbies = [
  {
    category: 'DEVELOPMENT',
    subcategories: [
      {
        name: 'Frontend',
        skills: ['HTML', 'CSS', 'JavaScript', 'React'],
      },
      {
        name: 'Backend',
        skills: ['Node.js', 'Python', 'Java', 'Database'],
      },
      {
        name: 'Mobile',
        skills: ['React Native', 'Flutter', 'Swift', 'Kotlin'],
      },
    ],
    image: developmentImg,
    alt: 'developmentImg',
  },
  {
    category: 'DESIGN',
    subcategories: [
      {
        name: 'UI/UX',
        skills: ['Figma', 'Adobe XD', 'Sketch', 'Photoshop'],
      },
      {
        name: 'Graphic',
        skills: ['Illustrator', 'InDesign', 'Typography', 'Branding'],
      },
      {
        name: 'Web Design',
        skills: ['Responsive', 'Wireframe', 'Prototype', 'User Research'],
      },
    ],
    image: 'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=300&fit=crop&crop=center',
    alt: 'designImg',
  },
  {
    category: 'MUSIC',
    subcategories: [
      {
        name: 'Production',
        skills: ['Logic Pro', 'Ableton', 'FL Studio', 'Mixing'],
      },
      {
        name: 'Instrument',
        skills: ['Piano', 'Guitar', 'Drums', 'Bass'],
      },
      {
        name: 'Theory',
        skills: ['Harmony', 'Composition', 'Arrangement', 'Sound Design'],
      },
    ],
    image: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=300&fit=crop&crop=center',
    alt: 'musicImg',
  },
  {
    category: 'PHOTOGRAPHY',
    subcategories: [
      {
        name: 'Digital',
        skills: ['Lightroom', 'VSCO', 'Composition', 'Editing'],
      },
      {
        name: 'Portrait',
        skills: ['Studio Lighting', 'Posing', 'Retouching', 'Fashion'],
      },
      {
        name: 'Landscape',
        skills: ['Nature', 'Long Exposure', 'HDR', 'Post Processing'],
      },
    ],
    image: 'https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=400&h=300&fit=crop&crop=center',
    alt: 'photographyImg',
  },
  {
    category: 'COOKING',
    subcategories: [
      {
        name: 'Baking',
        skills: ['Bread', 'Pastry', 'Cake', 'Desserts'],
      },
      {
        name: 'Asian',
        skills: ['Korean', 'Japanese', 'Chinese', 'Thai'],
      },
      {
        name: 'Western',
        skills: ['Italian', 'French', 'American', 'Mediterranean'],
      },
    ],
    image: 'https://images.unsplash.com/photo-1551892374-ecf8754cf8b0?w=400&h=300&fit=crop&crop=center',
    alt: 'cookingImg',
  },
  {
    category: 'FITNESS',
    subcategories: [
      {
        name: 'Strength',
        skills: ['Weightlifting', 'Calisthenics', 'Yoga', 'Nutrition'],
      },
      {
        name: 'Cardio',
        skills: ['Running', 'Cycling', 'Swimming', 'HIIT'],
      },
      {
        name: 'Flexibility',
        skills: ['Pilates', 'Stretching', 'Mobility', 'Balance'],
      },
    ],
    image: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=300&fit=crop&crop=center',
    alt: 'fitnessImg',
  },
  {
    category: 'LANGUAGE',
    subcategories: [
      {
        name: 'Speaking',
        skills: ['English', 'Japanese', 'Spanish', 'Korean'],
      },
      {
        name: 'Programming',
        skills: ['JavaScript', 'Python', 'Java', 'C++'],
      },
      {
        name: 'Ancient',
        skills: ['Latin', 'Greek', 'Sanskrit', 'Hebrew'],
      },
    ],
    image: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400&h=300&fit=crop&crop=center',
    alt: 'languageImg',
  },
  {
    category: 'CRAFT',
    subcategories: [
      {
        name: 'Handmade',
        skills: ['Knitting', 'Pottery', 'Woodwork', 'Jewelry'],
      },
      {
        name: 'Digital',
        skills: ['3D Printing', 'Laser Cutting', 'CAD', 'Electronics'],
      },
      {
        name: 'Traditional',
        skills: ['Calligraphy', 'Origami', 'Embroidery', 'Weaving'],
      },
    ],
    image: 'https://images.unsplash.com/photo-1452860606245-08befc0ff44b?w=400&h=300&fit=crop&crop=center',
    alt: 'craftImg',
  },
  {
    category: 'GAMING',
    subcategories: [
      {
        name: 'Development',
        skills: ['Unity', 'Unreal', 'C#', 'Game Design'],
      },
      {
        name: 'Esports',
        skills: ['Strategy', 'Mechanics', 'Teamwork', 'Analysis'],
      },
      {
        name: 'Content',
        skills: ['Streaming', 'Video Editing', 'Community', 'Reviews'],
      },
    ],
    image: 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=300&fit=crop&crop=center',
    alt: 'gamingImg',
  },
  {
    category: 'WRITING',
    subcategories: [
      {
        name: 'Creative',
        skills: ['Fiction', 'Poetry', 'Blogging', 'Screenwriting'],
      },
      {
        name: 'Technical',
        skills: ['Documentation', 'API Docs', 'Tutorials', 'Manuals'],
      },
      {
        name: 'Journalism',
        skills: ['Reviews', 'Interviewing', 'Research', 'Editing'],
      },
    ],
    image: 'https://images.unsplash.com/photo-1455390582262-044cdead277a?w=400&h=300&fit=crop&crop=center',
    alt: 'writingImg',
  },
];

const RoadmapPreview: FC = () => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedSubcategoryIndex, setSelectedSubcategoryIndex] = useState(0);
  const [isAnimating, setIsAnimating] = useState(false);

  const handlePrevious = () => {
    if (isAnimating) return;
    setIsAnimating(true);
    setCurrentIndex((prev) => (prev - 1 + hobbies.length) % hobbies.length);
    setSelectedSubcategoryIndex(0);
    setTimeout(() => setIsAnimating(false), 5);
  };

  const handleNext = () => {
    if (isAnimating) return;
    setIsAnimating(true);
    setCurrentIndex((prev) => (prev + 1) % hobbies.length);
    setSelectedSubcategoryIndex(0);
    setTimeout(() => setIsAnimating(false), 5);
  };

  const handleCardSelect = (targetIndex: number) => {
    if (isAnimating || targetIndex === 0) return;

    setIsAnimating(true);

    const newIndex = (currentIndex + targetIndex) % hobbies.length;
    setCurrentIndex(newIndex);
    setSelectedSubcategoryIndex(0);

    setTimeout(() => setIsAnimating(false), 800);
  };

  const getOrderedHobbies = () => {
    const orderedHobbies = [];
    for (let i = 0; i < Math.min(3, hobbies.length); i++) {
      const index = (currentIndex + i) % hobbies.length;
      orderedHobbies.push({ ...hobbies[index], displayIndex: i });
    }
    return orderedHobbies;
  };

  const orderedHobbies = getOrderedHobbies();
  const currentHobby = orderedHobbies[0];
  const currentSubcategory = currentHobby.subcategories[selectedSubcategoryIndex];

  const handleSubcategoryClick = (index: number) => {
    setSelectedSubcategoryIndex(index);
  };

  return (
    <div className="h-[calc(100vh-60px)] px-4 py-10">
      <div className="mb-0 text-center sm:mb-12">
        <h2 className="mb-2 text-4xl font-bold">로드맵 미리보기</h2>
        <p className="opacity-60">AI가 만들어준 로드맵을 미리 확인해 볼 수 있어요</p>
      </div>
      <div className="relative flex h-4/5">
        <button
          onClick={handlePrevious}
          disabled={isAnimating}
          className="flex h-full items-center justify-center disabled:opacity-50">
          <span className="material-symbols-outlined text-2xl">chevron_backward</span>
        </button>
        <div className="relative mx-auto flex w-3/4 items-center justify-center">
          {orderedHobbies.map((hobby, index) => {
            const baseScale = 1 - index * 0.08;
            const baseTranslateY = -(index * 10);
            const baseTranslateX = index * 15;
            const baseOpacity = Math.max(0.3, 1 - index * 0.15);
            const elevationY = -(index * index * 8);

            const zIndex = 3 - index;

            const transitionDuration = isAnimating ? '1000ms' : '300ms';
            const transitionTimingFunction = isAnimating ? 'cubic-bezier(0.175, 0.885, 0.32, 1.275)' : 'ease-out';

            const animationDelay = isAnimating ? `${index * 100}ms` : '0ms';

            let finalScale = baseScale;
            let finalTranslateY = baseTranslateY + elevationY;
            let finalTranslateX = baseTranslateX;
            let finalOpacity = baseOpacity;
            let finalRotateY = index * 2;
            let finalRotateZ = 0;

            if (isAnimating) {
              if (index === 0) {
                finalScale = 0.6;
                finalTranslateY = baseTranslateY + elevationY + 100;
                finalTranslateX = baseTranslateX + 120;
                finalOpacity = 0.2;
                finalRotateY = 45;
                finalRotateZ = -15;
              } else if (index === 1) {
                finalScale = 1.02;
                finalTranslateY = -5;
                finalTranslateX = -5;
                finalOpacity = 1;
                finalRotateY = 0;
                finalRotateZ = 2;
              } else if (index === 2) {
                const targetScale = 1 - 1 * 0.08;
                const targetTranslateY = -(1 * 25) - 1 * 1 * 8;
                const targetTranslateX = 1 * 15;
                const targetOpacity = Math.max(0.3, 1 - 1 * 0.15);

                finalScale = targetScale * 1.01;
                finalTranslateY = targetTranslateY - 3;
                finalTranslateX = targetTranslateX - 3;
                finalOpacity = targetOpacity;
                finalRotateY = 2;
                finalRotateZ = 1;
              }
            }

            return (
              <div
                key={`${hobby.category}-${currentIndex}-${index}`}
                onClick={() => handleCardSelect(index)}
                style={{
                  transform: `scale(${finalScale}) translateY(${finalTranslateY}px) translateX(${finalTranslateX}px) perspective(1000px) rotateY(${finalRotateY}deg) rotateZ(${finalRotateZ}deg)`,
                  zIndex: zIndex,
                  opacity: finalOpacity,
                  transitionDuration: transitionDuration,
                  transitionDelay: animationDelay,
                  transitionTimingFunction: transitionTimingFunction,
                  filter: `blur(${index * 0.5}px) brightness(${1 - index * 0.05})`,
                }}
                className={`aspect-5/3 absolute left-1/2 mx-auto grid w-full -translate-x-1/2 items-end rounded-2xl bg-gradient-to-br from-blue-400 via-cyan-300 to-indigo-400 p-2 shadow-2xl backdrop-blur-sm transition-all sm:grid-cols-2 sm:p-6 ${
                  index > 0 && !isAnimating ? 'cursor-pointer hover:scale-105' : ''
                } ${isAnimating ? 'pointer-events-none' : ''}`}>
                <div className="relative z-10 sm:py-4">
                  <h6 className="pb-2 text-2xl font-black uppercase drop-shadow-lg sm:p-4">{hobby.category}</h6>
                  <div className="flex flex-wrap gap-2 px-2 py-2">
                    {hobby.subcategories.map((subcategory, subIndex) => (
                      <span
                        key={subIndex}
                        onClick={(e) => {
                          e.stopPropagation();
                          if (index === 0 && !isAnimating) {
                            handleSubcategoryClick(subIndex);
                          }
                        }}
                        className={`text-md inline-block transform cursor-pointer rounded-full border px-4 py-2 font-semibold uppercase transition-all hover:scale-105 ${
                          index === 0 && selectedSubcategoryIndex === subIndex
                            ? 'border-gray-800 bg-gray-800 text-white shadow-lg'
                            : 'border-gray-600/30 hover:bg-gray-800 hover:text-white hover:shadow-md'
                        } ${isAnimating || index > 0 ? 'pointer-events-none' : ''}`}>
                        {subcategory.name}
                      </span>
                    ))}
                  </div>
                  <div className="flex flex-wrap gap-2 px-2.5">
                    {index === 0 &&
                      currentSubcategory.skills.map((skill, skillIndex) => (
                        <span
                          key={skillIndex}
                          className="inline-block transform cursor-pointer rounded-full border border-gray-600/30 px-3 py-1 text-sm uppercase transition-all hover:scale-105 hover:bg-white/10">
                          {skill}
                        </span>
                      ))}
                  </div>
                </div>
                <div className="relative h-full w-full overflow-hidden rounded-2xl">
                  <img alt={hobby.alt} className="h-full object-cover object-center" src={hobby.image} />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent"></div>
                </div>
                {index > 0 && !isAnimating && (
                  <div className="absolute inset-0 rounded-2xl border-2 border-transparent transition-all duration-300 hover:border-white/30" />
                )}
              </div>
            );
          })}
        </div>
        <button
          onClick={handleNext}
          disabled={isAnimating}
          className="flex h-full items-center justify-center disabled:opacity-50">
          <span className="material-symbols-outlined text-2xl">chevron_forward</span>
        </button>
      </div>
    </div>
  );
};

export default RoadmapPreview;
