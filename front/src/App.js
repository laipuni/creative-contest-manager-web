import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Main from "./main";
import NotFound from "./notFound/notFound";

function App() {
  return (
    <div className="App">
      <BrowserRouter>
          <Routes>
              <Route path="*" element={<NotFound />}></Route>
          </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
