package seng302.group3.model.io.property_sheets;

import org.junit.*;
import seng302.group3.JavaFXThreadingRule;
import seng302.group3.model.Organisation;
import seng302.group3.model.Skill;

/**
 * Created by Eddy on 29-May-15.
 * A testing suite for Skill Sheet (the dialog for creating and editing skills)
 */
public class SkillSheetTest {

    private Organisation testOrganisation;
    private SkillSheet skillSheet;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Before
    public void setUp() throws Exception{
        testOrganisation = new Organisation("testOrg");
        skillSheet = new SkillSheet();
    }

    @After
    public void tearDown() throws Exception{
        testOrganisation = null;
        skillSheet = null;
    }

    @Test
    public void TestNullNameTextFieldOnValidate() throws Exception{
        skillSheet.nameTextField.setText("");
        skillSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("The validate should fail with an empty sting"
            , skillSheet.validate(testOrganisation));
    }

    @Test
    public void TestNonNullNameTextFieldOnValidate() throws Exception{
        skillSheet.nameTextField.setText("ValidName");
        skillSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertTrue("The validate should pass with both non-null stings"
            , skillSheet.validate(testOrganisation));
    }

    @Test
    public void TestNullDescriptionTextFieldOnValidate() throws Exception{
        skillSheet.nameTextField.setText("ValidName");
        skillSheet.descriptionTextField.setText("");

        Assert.assertFalse("The validate should fail with an empty sting"
            , skillSheet.validate(testOrganisation));
    }

    @Test
    public void TestNonUniqueNameOnValidate() throws Exception{
        Skill firstSkill = new Skill("ValidName","ValidDescription");
        testOrganisation.addSkill(firstSkill);

        skillSheet.nameTextField.setText("ValidName");
        skillSheet.descriptionTextField.setText("ValidDescription2");

        Assert.assertFalse("The validate should fail with a non-unqiue name"
            , skillSheet.validate(testOrganisation));
    }

    @Test
    public void TestUniqueNameOnValidate() throws Exception{
        Skill firstSkill = new Skill("ValidName","ValidDescription");
        testOrganisation.addSkill(firstSkill);

        skillSheet.nameTextField.setText("StillValidName");
        skillSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertTrue("The validate should pass with a unique name"
            , skillSheet.validate(testOrganisation));
    }

    @Test
    public void TestAbilityToMakeANewPOSkill() throws Exception{
        skillSheet.nameTextField.setText("PO");
        skillSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("The validate should fail with a name 'PO'"
            , skillSheet.validate(testOrganisation));
    }

    @Test
    public void TestAbilityToMakeANewSMSkill() throws Exception{
        skillSheet.nameTextField.setText("SM");
        skillSheet.descriptionTextField.setText("ValidDescription");

        Assert.assertFalse("The validate should fail with a name 'SM'"
            , skillSheet.validate(testOrganisation));
    }

    @Test
    public void TestLoadingASkillNameToBeEdited(){
        Skill testSkill = new Skill("ValidName","ValidDescription");
        testOrganisation.addSkill(testSkill);

        skillSheet.fieldConstruct(testSkill);

        Assert.assertEquals("The nameTextField should be equal to 'ValidName'"
                , skillSheet.nameTextField.getText(), ("ValidName"));
    }

    @Test
    public void TestLoadingASkillDescriptionToBeEdited(){
        Skill testSkill = new Skill("ValidName","ValidDescription");
        testOrganisation.addSkill(testSkill);

        skillSheet.fieldConstruct(testSkill);

        Assert.assertEquals("The descriptionTextField should be equal to 'ValidDescription'"
                , skillSheet.descriptionTextField.getText(), ("ValidDescription"));
    }

    @Test
    public void TestLoadingTheSMSkillToBeEdited(){
        Skill testSkill = new Skill("SM","SMDescription");
        testOrganisation.addSkill(testSkill);
        skillSheet.fieldConstruct(testSkill);
        Assert.assertTrue("When SM is the name of a skill the nameTextField should be disabled"
                ,skillSheet.nameTextField.isDisabled());
    }

    @Test
    public void TestLoadingTheSPOSkillToBeEdited(){
        Skill testSkill = new Skill("PO","PODescription");
        testOrganisation.addSkill(testSkill);
        skillSheet.fieldConstruct(testSkill);
        Assert.assertTrue("When PO is the name of a skill the nameTextField should be disabled"
                , skillSheet.nameTextField.isDisabled());
    }

    @Test
    public void TestValidationWhenASkillHasBeenPassedToBeConstructed(){
        Skill testSkill = new Skill("ValidName","ValidDescription");
        testOrganisation.addSkill(testSkill);
        skillSheet.fieldConstruct(testSkill);
        Assert.assertTrue("The skill should not conflict with its own name in the organisation"
                , skillSheet.validate(testOrganisation, testSkill));
    }

    @Test
    public void TestApplyNameInOrganisation(){
        skillSheet.nameTextField.setText("ValidName");
        skillSheet.descriptionTextField.setText("ValidDescription");

        Skill newSkill = new Skill();

        skillSheet.apply(newSkill);
        Assert.assertTrue("The skill should have had the nameTextField applied to its short name field"
                ,newSkill.getShortName().equals("ValidName"));
    }

    @Test
    public void TestApplyForDescription(){
        skillSheet.nameTextField.setText("ValidName");
        skillSheet.descriptionTextField.setText("ValidDescription");

        Skill newSkill = new Skill();

        skillSheet.apply(newSkill);
        Assert.assertTrue("The skill should have had the descriptionTextField applied to its description field"
                ,newSkill.getDescription().equals("ValidDescription"));
    }

    @Test
    public void TestApplyOnAPreviouslyMadeSkillName(){
        Skill newSkill = new Skill("TestName","TestDescription");
        testOrganisation.addSkill(newSkill);
        skillSheet.fieldConstruct(newSkill);

        skillSheet.nameTextField.setText("ValidName");
        skillSheet.descriptionTextField.setText("ValidDescription");

        skillSheet.apply(newSkill);
        Assert.assertTrue("The skill should have had the nameTextField applied to its short name field"
                ,newSkill.getShortName().equals("ValidName"));
    }

    @Test
    public void TestApplyOnAPreviouslyMadeSkillDescription(){
        Skill newSkill = new Skill("TestName","TestDescription");
        testOrganisation.addSkill(newSkill);
        skillSheet.fieldConstruct(newSkill);

        skillSheet.nameTextField.setText("ValidName");
        skillSheet.descriptionTextField.setText("ValidDescription");

        skillSheet.apply(newSkill);
        Assert.assertTrue("The skill should have had the descriptionTextField applied to its description field"
                ,newSkill.getDescription().equals("ValidDescription"));
    }

}
