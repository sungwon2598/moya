// src/utils/cn.ts
import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * Utility function to merge Tailwind CSS classes and handle conditional classes
 * @param inputs - Class values to merge
 * @returns Merged class string
 *
 * @example
 * // Basic usage
 * cn('px-4 py-2', 'bg-blue-500') // => 'px-4 py-2 bg-blue-500'
 *
 * // With conditions
 * cn('px-4 py-2', isActive && 'bg-blue-500') // => 'px-4 py-2 bg-blue-500' or 'px-4 py-2'
 *
 * // With Tailwind conflicts resolution
 * cn('px-2 py-1 p-3') // => 'p-3'
 */
export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}