const baseSrvUrl = 'http://kubernetes.docker.internal/api/tasks/api/v1';
export const environment = {
  title: 'Week Planner',
  production: true,
  tasksSrvUrl: `${baseSrvUrl}`,
  selectedTenant: '',
  verGUI: '0.0.1.prod',
  dateGUI: 'Jan-05-2024'
};
