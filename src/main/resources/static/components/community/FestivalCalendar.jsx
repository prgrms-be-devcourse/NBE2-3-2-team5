import React, { useState, useEffect } from 'react';
import Calendar from 'react-calendar';
import "react-calendar/dist/Calendar.css";

const FestivalCalendar = () => {
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [festivals, setFestivals] = useState([]);
    const [loading, setLoading] = useState(false);
    const [selectedDateFestivals, setSelectedDateFestivals] = useState([]);

    const fetchFestivalsByMonth = async (year, month) => {
        setLoading(true);
        console.log(`Fetching festivals for ${year}-${month}`);
        try {
            const response = await fetch(`/api/events/filter/month?year=${year}&month=${month}&page=0&size=100`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            console.log('Fetched festivals:', data);
            setFestivals(data.content || []);
        } catch (error) {
            console.error("Error fetching festivals:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleDateChange = (date) => {
        setSelectedDate(date);
        const formattedDate = date.toISOString().split('T')[0];

        const eventsOnDate = festivals.filter((festival) => {
            const startDate = new Date(festival.startDate).toISOString().split('T')[0];
            const endDate = new Date(festival.endDate).toISOString().split('T')[0];
            return startDate <= formattedDate && endDate >= formattedDate;
        });

        setSelectedDateFestivals(eventsOnDate);
    };

    const renderTileContent = ({ date }) => {
        const formattedDate = date.toISOString().split('T')[0];
        const eventsOnDate = festivals.filter((festival) => {
            const startDate = new Date(festival.startDate).toISOString().split('T')[0];
            const endDate = new Date(festival.endDate).toISOString().split('T')[0];
            return startDate <= formattedDate && endDate >= formattedDate;
        });

        return eventsOnDate.length > 0 ? (
            <div className="text-xs mt-1">
                <div className="h-1 w-1 bg-custom-purple rounded-full mx-auto"></div>
                <div className="text-custom-purple mt-1">{eventsOnDate.length}개</div>
            </div>
        ) : null;
    };

    useEffect(() => {
        const year = selectedDate.getFullYear();
        const month = selectedDate.getMonth() + 1;
        fetchFestivalsByMonth(year, month);
    }, [selectedDate]);

    return (
        <div className="max-w-7xl mx-auto bg-white rounded-lg shadow-lg">
            <div className="p-8">
                <h2 className="text-2xl font-bold mb-6 text-custom-purple text-center">축제 달력</h2>

                {/* 캘린더 섹션 */}
                <div className="mb-8">
                    <Calendar
                        onChange={handleDateChange}
                        value={selectedDate}
                        tileContent={renderTileContent}
                        className="w-full shadow-md rounded-lg"
                    />
                </div>

                {/* 축제 목록 섹션 */}
                <div className="mt-8">
                    <h3 className="text-xl font-semibold text-custom-purple mb-4 flex items-center">
                        <span className="bg-custom-purple w-2 h-6 mr-2 rounded"></span>
                        {selectedDate.toLocaleDateString('ko-KR', {
                            year: 'numeric',
                            month: 'long',
                            day: 'numeric'
                        })}의 축제
                    </h3>

                    {loading ? (
                        <div className="text-center py-8">
                            <div className="animate-pulse text-gray-500">로딩 중...</div>
                        </div>
                    ) : selectedDateFestivals.length > 0 ? (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                            {selectedDateFestivals.map((festival) => (
                                <div
                                    key={festival.festival_id}
                                    className="p-4 bg-white border border-gray-100 rounded-lg hover:border-custom-purple
                                             transition-all duration-200 hover:shadow-md"
                                >
                                    <h4 className="font-medium text-custom-purple text-lg mb-2">
                                        {festival.title}
                                    </h4>
                                    <p className="text-gray-600 mb-2">{festival.address}</p>
                                    <div className="text-sm text-gray-500 flex items-center">
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none"
                                             viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                        </svg>
                                        {new Date(festival.startDate).toLocaleDateString()} ~{' '}
                                        {new Date(festival.endDate).toLocaleDateString()}
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-8 bg-gray-50 rounded-lg">
                            <p className="text-gray-500">이 날짜에 예정된 축제가 없습니다.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default FestivalCalendar;