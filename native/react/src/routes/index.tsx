import { NavigationContainer } from "@react-navigation/native";
import React from "react";

import MainRoutes from "./Main";

export default function LoggingRoutes() {
  return (
    <NavigationContainer>
      <MainRoutes />
    </NavigationContainer>
  );
}
