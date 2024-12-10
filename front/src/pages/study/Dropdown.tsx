import { useState, useRef, useEffect } from 'react';
import { ChevronDown, ChevronRight } from 'lucide-react';

interface Category {
    id: number;
    name: string;
    subCategories: Category[];
}

interface DropdownProps {
    label: string;
    options?: { value: string; label: string }[];
    categories?: Category[];
    onChange: (value: string) => void;
    value?: string;
    isCategory?: boolean;
}

const Dropdown = ({ label, options, categories, onChange, value, isCategory }: DropdownProps) => {
    const [isOpen, setIsOpen] = useState(false);
    const [activeCategory, setActiveCategory] = useState<number | null>(null);
    const dropdownRef = useRef<HTMLDivElement>(null);

    const selectedOption = !isCategory && options?.find(option => option.value === value);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsOpen(false);
                setActiveCategory(null);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const handleCategorySelect = (categoryName: string, subCategoryName?: string) => {
        onChange(subCategoryName || categoryName);
        setIsOpen(false);
        setActiveCategory(null);
    };

    const renderCategoryMenu = () => {
        if (!categories) return null;

        return (
            <div className="absolute z-10 w-64 mt-2 bg-white border border-gray-200 rounded-lg shadow-lg py-1">
                {categories.map((category) => (
                    <div
                        key={category.id}
                        className="relative"
                        onMouseEnter={() => setActiveCategory(category.id)}
                    >
                        <button
                            onClick={() => handleCategorySelect(category.name)}
                            className={`w-full px-4 py-2 text-left hover:bg-gray-50 transition-colors duration-150 flex items-center justify-between
                                ${value === category.name ? 'bg-blue-50 text-blue-600' : 'text-gray-700'}`}
                        >
                            {category.name}
                            {category.subCategories.length > 0 && (
                                <ChevronRight className="w-4 h-4" />
                            )}
                        </button>
                        {category.subCategories.length > 0 && activeCategory === category.id && (
                            <div className="absolute left-full top-0 w-48 bg-white border border-gray-200 rounded-lg shadow-lg py-1 -ml-1">
                                {category.subCategories.map((subCategory) => (
                                    <button
                                        key={subCategory.id}
                                        onClick={() => handleCategorySelect(category.name, subCategory.name)}
                                        className={`w-full px-4 py-2 text-left hover:bg-gray-50 transition-colors duration-150
                                            ${value === subCategory.name ? 'bg-blue-50 text-blue-600' : 'text-gray-700'}`}
                                    >
                                        {subCategory.name}
                                    </button>
                                ))}
                            </div>
                        )}
                    </div>
                ))}
            </div>
        );
    };

    const renderRegularMenu = () => {
        if (!options) return null;

        return (
            <div className="absolute z-10 w-full mt-2 bg-white border border-gray-200 rounded-lg shadow-lg py-1">
                {options.map((option) => (
                    <button
                        key={option.value}
                        onClick={() => {
                            onChange(option.value);
                            setIsOpen(false);
                        }}
                        className={`w-full px-4 py-2 text-left hover:bg-gray-50 transition-colors duration-150
                            ${value === option.value ? 'bg-blue-50 text-blue-600' : 'text-gray-700'}`}
                    >
                        {option.label}
                    </button>
                ))}
            </div>
        );
    };

    return (
        <div className="relative" ref={dropdownRef}>
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="px-4 py-2 w-40 rounded-full border border-gray-200 bg-white hover:border-gray-300
                     focus:outline-none focus:ring-2 focus:ring-blue-100 transition-colors duration-200
                     flex items-center justify-between text-gray-700"
            >
                <span className="truncate">
                    {isCategory
                        ? (value || label)
                        : (selectedOption ? selectedOption.label : label)
                    }
                </span>
                <ChevronDown
                    className={`w-4 h-4 ml-2 transition-transform duration-200 ${isOpen ? 'transform rotate-180' : ''}`}
                />
            </button>

            {isOpen && (isCategory ? renderCategoryMenu() : renderRegularMenu())}
        </div>
    );
};

export default Dropdown;
