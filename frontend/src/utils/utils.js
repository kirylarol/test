import {DATE_PRECISIONS} from "../constants/Constants";

export const parseDate = (date)=>{
    if (isNaN(date.getDate())) return ""
    return `${date.getDate().toString().padStart(2, '0')}.${(date.getMonth()+1).toString().padStart(2, '0')}.${date.getFullYear()}`
}

export const convertLongToDate = (date)=>{
    return new Date(date*24*60*60*1000)
}

export const convertToDate = (date,precision)=>{
    if (precision === DATE_PRECISIONS.DAY) return parseDate(date)
    if (precision === DATE_PRECISIONS.WEEK) return (date.getDate()-date.getDay()+1).toString().padStart(2,'0') + '.'+(parseInt(date.getMonth())+1).toString().padStart(2,'0') +'.' + date.getFullYear() +'-'+ (date.getDate()-date.getDay()+8).toString().padStart(2,'0') + '.'+(parseInt(date.getMonth())+1).toString().padStart(2,'0') +'.' + date.getFullYear()
    if (precision === DATE_PRECISIONS.MONTH) return (date.getMonth()+1).toString().padStart(2,'0') + '.' + date.getFullYear()
    if (precision === DATE_PRECISIONS.YEAR) return date.getFullYear()
    return null;
}

