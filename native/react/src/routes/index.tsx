import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import MainRoutes from './Main';

export default function LoggingRoutes() {
  return (
    <NavigationContainer>
      <MainRoutes />
    </NavigationContainer>
  );
}
