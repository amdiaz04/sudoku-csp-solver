// Tab layout for switching between the solver and performance pages.

import SolverPage from "./SolverPage";
import PerformancePage from "./PerformancePage";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

function Navigation() {
  return (
    <Tabs defaultValue="solver">
      <TabsList
        variant="line"
        className="sticky top-0 z-10 w-full group-data-horizontal/tabs:h-auto justify-start bg-background px-4 pt-2 md:px-6"
      >
        <TabsTrigger value="solver" className="h-11 flex-none px-4 md:h-8">
          Solver
        </TabsTrigger>
        <TabsTrigger value="performance" className="h-11 flex-none px-4 md:h-8">
          Statistics
        </TabsTrigger>
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
