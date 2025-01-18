import React, { useState } from "react";

interface SearchBoxProps {
    placeholder: string;
    onSearch: (query: string) => void;
}

const SearchBox: React.FC<SearchBoxProps> = ({ placeholder, onSearch }) => {
    const [query, setQuery] = useState("");

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setQuery(value);
        onSearch(value);
    };

    return (
        <input
            type="text"
            value={query}
            onChange={handleInputChange}
            placeholder={placeholder}
            className="w-full px-3 py-2 border rounded-md"
            style={{ maxWidth: "200px" }}
        />
    );
};

export default SearchBox;