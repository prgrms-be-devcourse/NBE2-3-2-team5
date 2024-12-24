/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./src/main/resources/static/**/*.{js,jsx,ts,tsx}",
        "./src/main/resources/templates/**/*.{html,js}"
    ],
    theme: {
        extend: {
            colors: {
                'custom-purple': '#4D4B88',
            },
        },
    },
    plugins: [
        require('@tailwindcss/line-clamp'),
    ],
}