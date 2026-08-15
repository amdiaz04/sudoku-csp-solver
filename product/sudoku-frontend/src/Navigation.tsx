// Tab layout for switching between the solver and performance pages.

import SolverPage from "./SolverPage";
import PerformancePage from "./PerformancePage";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

function Navigation() {
  return (
    <Tabs defaultValue="solver">
      <TabsList variant="line">
        <TabsTrigger value="solver">Solver</TabsTrigger>
        <TabsTrigger value="performance">Statistics</TabsTrigger>
      </TabsList>
      <TabsContent value="solver">
        <SolverPage />
      </TabsContent>
      <TabsContent value="performance">
        <PerformancePage />
      </TabsContent>
    </Tabs>
  ); 
}

export default Navigation;
