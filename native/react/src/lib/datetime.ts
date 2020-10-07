import dayjs from 'dayjs';

export function formatDate(timestamp: string): string {
  return dayjs(timestamp).format("YYYY/MM/DD");
}

export function formatDateTime(timestamp: string): string {
  return dayjs(timestamp).format("YYYY/MM/DD HH:mm:ss");
}
