
package hudson.plugins.logfilesizechecker;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;

import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.recipes.LocalData;


public class LogfilesizecheckerWrapperIntegrationTest extends HudsonTestCase {

    @LocalData
    public void test1() throws Exception {
        //maxLogSize=1MB, failBuild=true, setOwn=true 
        FreeStyleProject project = (FreeStyleProject) hudson.getItem("1");
        project.getBuildWrappersList().add(new LogfilesizecheckerWrapper(1,true,true));

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        System.out.println("LogFileLength: " + build.getLogFile().length());
        
        assertBuildStatus(Result.FAILURE, build);
    }
    
    @LocalData
    public void test2() throws Exception {
        //maxLogSize=1MB, failBuild=false, setOwn=true 
        FreeStyleProject project = (FreeStyleProject) hudson.getItem("1");
        project.getBuildWrappersList().add(new LogfilesizecheckerWrapper(1,false,true));

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        System.out.println("LogFileLength: " + build.getLogFile().length());
        
        assertBuildStatus(Result.ABORTED, build);
    }
    
    @LocalData
    public void test3() throws Exception {
        //maxLogSize=1MB, failBuild=false, setOwn=false 
        FreeStyleProject project = (FreeStyleProject) hudson.getItem("1");
        project.getBuildWrappersList().add(new LogfilesizecheckerWrapper(1,false,false));

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        System.out.println("LogFileLength: " + build.getLogFile().length());
        
        assertBuildStatus(Result.SUCCESS, build);
    }
    
    @LocalData
    public void test4() throws Exception {
        //maxLogSize=5MB, failBuild=false, setOwn=true 
        FreeStyleProject project = (FreeStyleProject) hudson.getItem("1");
        project.getBuildWrappersList().add(new LogfilesizecheckerWrapper(5,false,true));

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        System.out.println("LogFileLength: " + build.getLogFile().length());
        
        assertBuildStatus(Result.SUCCESS, build);
    }

    @LocalData
    public void test5() throws Exception {
        //maxLogSize=0MB, failBuild=true, setOwn=false, defaultLogSize=1
        FreeStyleProject project = (FreeStyleProject) hudson.getItem("1");
        LogfilesizecheckerWrapper buildWrapper = new LogfilesizecheckerWrapper(0,true,false);
        ((LogfilesizecheckerWrapper.DescriptorImpl) buildWrapper.getDescriptor()).setDefaultLogSize(1);
        project.getBuildWrappersList().add(buildWrapper);

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        System.out.println("LogFileLength: " + build.getLogFile().length());
        
        assertBuildStatus(Result.FAILURE, build);
    }
    
    //configuration round trip test
    public void testConfigRoundTrip() throws Exception {
        FreeStyleProject p = createFreeStyleProject();
        LogfilesizecheckerWrapper before = new LogfilesizecheckerWrapper(3,true,true);
        p.getBuildWrappersList().add(before);
        
        submit(new WebClient().goTo("configure").getFormByName("config"));
        LogfilesizecheckerWrapper after = p.getBuildWrappersList().get(LogfilesizecheckerWrapper.class);
        
        assertEqualBeans(before,after,"maxLogSize,failBuild,setOwn");
        //assertEqualDataBoundBeans(before, after)
    }
}
