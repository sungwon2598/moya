// import { useState } from 'react';
// import { format } from 'date-fns';
// import { ko } from 'date-fns/locale';
// import { DayPicker } from 'react-day-picker';
// import { CalendarIcon } from 'lucide-react';
// import 'react-day-picker/dist/style.css'; // 기본 스타일 가져오기

// export function DayPickerCalendar() {
//   const [selected, setSelected] = useState();
//   const [isCalendarOpen, setIsCalendarOpen] = useState(false);

//   const toggleCalendar = () => {
//     setIsCalendarOpen(!isCalendarOpen);
//   };

//   const handleDaySelect = (day) => {
//     setSelected(day);
//     setIsCalendarOpen(false);
//   };

//   // 현재 날짜보다 미래 날짜와 1900년 이전 날짜는 선택 불가능하게 설정
//   const disabledDays = [{ after: new Date() }, { before: new Date('1900-01-01') }];

//   return (
//     <div className="flex flex-col space-y-4">
//       <div className="relative">
//         <button
//           type="button"
//           onClick={toggleCalendar}
//           className="flex w-[240px] items-center justify-between rounded-md border px-3 py-2 text-left focus:outline-none">
//           {selected ? format(selected, 'PPP', { locale: ko }) : <span className="text-gray-500">날짜 선택</span>}
//           <CalendarIcon className="w-4 h-4 opacity-50" />
//         </button>

//         {isCalendarOpen && (
//           <div className="absolute z-10 p-2 mt-1 bg-white border rounded-md shadow-lg">
//             <DayPicker
//               mode="single"
//               selected={selected}
//               onSelect={handleDaySelect}
//               disabled={disabledDays}
//               locale={ko}
//               showOutsideDays
//               fixedWeeks
//             />
//           </div>
//         )}
//       </div>

//       <p className="text-sm text-gray-500">생년월일은 나이 계산에 사용됩니다.</p>

//       {selected && (
//         <button
//           type="button"
//           onClick={() => alert(JSON.stringify({ dob: selected }))}
//           className="px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700">
//           제출
//         </button>
//       )}
//     </div>
//   );
// }
